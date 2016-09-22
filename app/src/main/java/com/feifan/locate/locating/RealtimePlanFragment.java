package com.feifan.locate.locating;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.R;
import com.feifan.locate.locating.config.LocatingPanel;
import com.feifan.locate.provider.LocateData.Zone;
import com.feifan.locate.widget.cursorwork.AbsLoaderFragment;
import com.feifan.locate.widget.cursorwork.ICursorAdapter;
import com.feifan.locate.widget.ui.BaseFragment;
import com.feifan.locate.widget.ui.pager.TextIndicator;
import com.feifan.locate.widget.ui.pager.VerticalViewPager;
import com.feifan.locatelib.network.HttpResult;
import com.feifan.locatelib.network.HttpResultSubscriber;
import com.feifan.locatelib.network.ServiceFactory;
import com.feifan.locatelib.network.TransformUtils;
import com.feifan.locatelib.online.LocateInfo;
import com.feifan.locatelib.online.LocateQueryData;
import com.feifan.locatelib.online.RxFingerLocateService;
import com.feifan.planlib.PlanView;
import com.feifan.scanlib.BeaconNotifier;
import com.feifan.scanlib.ScanManager;
import com.feifan.scanlib.beacon.SampleBeacon;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * 用于实时定位的平面图Fragment
 *
 * Created by xuchunlei on 16/9/18.
 */
public class RealtimePlanFragment extends AbsLoaderFragment implements OnSharedPreferenceChangeListener {

    public static final String EXTRA_KEY_BUILDING = "building";

    @IdRes
    private static final int ID_MENU_SETTING = 1;

    // data
    private static final int LOADER_ID = 5; // 使用异步加载组件时分配的ID，不能与其他数据使用的ID相同
    private PlanPagerAdapter mAdapter;

    // view
    private VerticalViewPager mPager;
    private TextIndicator mIndicator;
    private LocatingPanel mPanel;

    // scan
    private ScanManager mScanManager = ScanManager.getInstance();
    private int mScanPeriod = 3000;

    // network
    private LocateQueryData queryData = new LocateQueryData();
    private Observable<Long> queryEngine;
    private Subscription querySubscription;
    private RxFingerLocateService locateService;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ServiceFactory.getInstance().initialize(getContext());

        // scan
        mScanManager.setAutoStart(true, context.getPackageName(), mScanPeriod);
        mScanManager.bind(context);

        // query
        queryData.algorithm = "centroid";
        queryData.position = "A22";
        locateService = ServiceFactory.getInstance().createService(RxFingerLocateService.class);
        startQueryAtInterval(3);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_realtime_plan, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BuildingModel buildingModel = getArguments().getParcelable(EXTRA_KEY_BUILDING);
        setTitle(buildingModel.name);

        mPager = findView(R.id.realtime_plan_pager);
        mAdapter = new PlanPagerAdapter(getContext(), PlanView.class);
        mPager.setAdapter(mAdapter);
        mIndicator = findView(R.id.realtime_plan_indicator);

        mPanel = (LocatingPanel) getLayoutInflater(savedInstanceState).inflate(R.layout.layout_locating_panel,
                null, false);
        mPanel.setConfigChangeListener(this);

//        Handler h = new Handler(){
//            int curPos = 0;
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
////                mPager.setCurrentItem(curPos % 6);
//                mPager.setCurrentItem(curPos++ % 6, false);
//                sendEmptyMessageDelayed(9, 2000);
//            }
//        };
//        h.sendEmptyMessage(9);

    }

    /**
     * 生成Loader的ID
     */
    @Override
    protected int getLoaderId() {
        return LOADER_ID;
    }

    /**
     * 获得ContentProvider的Uri
     */
    @Override
    protected Uri getContentUri() {
        return Zone.CONTENT_URI;
    }

    /**
     * 获得Adapter
     */
    @Override
    protected <A extends ICursorAdapter> A getAdapter() {
        return (A)mAdapter;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        super.onLoadFinished(loader, data);
        mIndicator.setupWithViewPager(mPager);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mScanManager.setNotifier(new BeaconNotifier() {
            @Override
            public void onBeaconsReceived(Collection<SampleBeacon> beacons) {
                LogUtils.i("we got " + (beacons == null ? 0 : beacons.size()) + " beacon samples");
                Map<String, Float> sData = DataProcessor.processBeaconData(beacons);
                queryData.upDateTensor(sData);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mScanManager.unBind(getContext());
        if(!querySubscription.isUnsubscribed()){
            querySubscription.unsubscribe();
        }
    }

    @Override
    protected List<MenuInfo> getMenuList() {
        List<MenuInfo> infos = super.getMenuList();
        infos.add(new MenuInfo(ID_MENU_SETTING, BaseFragment.NO_RES, R.string.realtime_plan_menu_setting_text));
        return infos;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case ID_MENU_SETTING:
                if(mPanel.isShown()) {
                    mPanel.hide();
                }else {
                    mPanel.show(getActivity().getWindow());
                }
                return true;
        }

        return super.onMenuItemClick(item);
    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     * <p>
     * <p>This callback will be run on your main thread.
     *
     * @param sharedPreferences The {@link SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(LocatingPanel.KEY_SCAN_PERIOD)) {
            int period = Integer.valueOf(sharedPreferences.getString(key, "3"));
            mScanManager.stop();
            mScanManager.start(getContext().getPackageName(), period * 1000);
        } else if(key.equals(LocatingPanel.KEY_REQUEST_PERIOD)) {
            int period = Integer.valueOf(sharedPreferences.getString(key, "3"));
            if(!querySubscription.isUnsubscribed()) {
                querySubscription.unsubscribe();
            }
            startQueryAtInterval(period);
        }
    }

    private void startQueryAtInterval(int interval) {
        queryEngine = Observable.interval(0, interval, TimeUnit.SECONDS);
        querySubscription = queryEngine.subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                locateService.getLocation(queryData)
                        .compose(TransformUtils.<HttpResult<LocateInfo>>defaultSchedulers())
                        .subscribe(new HttpResultSubscriber<LocateInfo>() {

                            @Override
                            protected void _onError(Throwable e) {
                                LogUtils.e(e.getMessage());
                            }

                            @Override
                            protected void _onSuccess(LocateInfo data) {
                                if (data != null) {
                                    LogUtils.e(data.x + "," + data.y + " at " + System.currentTimeMillis());
                                }
                            }
                        });
            }
        });
    }
}
