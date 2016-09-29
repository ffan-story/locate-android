package com.feifan.locate.locating;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.R;
import com.feifan.locate.common.BuildingFragment;
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
import com.feifan.planlib.layer.TraceLayer;
import com.feifan.scanlib.BeaconNotifier;
import com.feifan.scanlib.ScanManager;
import com.feifan.scanlib.beacon.SampleBeacon;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
    private transient LocateQueryData queryData = new LocateQueryData();
    private Observable<Long> queryEngine;
    private Subscription querySubscription;
    private RxFingerLocateService locateService;

    // locate
    private int currentFloor = -100;
    private TraceLayer currentPlanLayer;
    private int floorCount = 0;
    private int minFloor = 0;
    private int computeFloor; // 用于计算的楼层参数

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ServiceFactory.getInstance().initialize(getContext());
        BuildingModel buildingModel = getArguments().getParcelable(BuildingFragment.EXTRA_KEY_BUILDING);
        setTitle(buildingModel.name);
        minFloor = buildingModel.minFloor;

        // scan
        mScanManager.setAutoStart(true, context.getPackageName(), mScanPeriod);
        mScanManager.bind(context);

        // query
        queryData.algorithm = "centroid";
        queryData.position = buildingModel.code;
        locateService = ServiceFactory.getInstance().createService(RxFingerLocateService.class);
        startQueryAtInterval(3);
        LogUtils.i("we are locating in " + queryData.position + " now");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_realtime_plan, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPager = findView(R.id.realtime_plan_pager);
        mAdapter = new PlanPagerAdapter(getContext(), PlanView.class);
        mPager.setAdapter(mAdapter);
        mIndicator = findView(R.id.realtime_plan_indicator);

        mPanel = (LocatingPanel) getLayoutInflater(savedInstanceState).inflate(R.layout.layout_locating_panel,
                null, false);
        mPanel.setConfigChangeListener(this);
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
        floorCount = data.getCount();
        computeFloor = floorCount + minFloor;
        LogUtils.d("floorCount=" + floorCount + ",minFloor=" + minFloor + ",computeFloor=" + computeFloor);

        // test
//        simulate();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mScanManager.setNotifier(new BeaconNotifier() {
            @Override
            public void onBeaconsReceived(Collection<SampleBeacon> beacons) {
                LogUtils.i("we got " + (beacons == null ? 0 : beacons.size()) + " beacon samples");
                Map<String, Float> sData = DataProcessor.processBeaconData(queryData.position, beacons);
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
        } else if(key.equals(LocatingPanel.KEY_ALGORITHM)) {
            queryData.algorithm = sharedPreferences.getString(key, queryData.algorithm);
        }
    }

    private void startQueryAtInterval(int interval) {
        queryEngine = Observable.interval(0, interval, TimeUnit.SECONDS);
        querySubscription = queryEngine.subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                if(!queryData.tensor.isEmpty()) {
                    locateService.getLocation(queryData)
                            .compose(TransformUtils.<HttpResult<LocateInfo>>defaultSchedulers())
                            .subscribe(new HttpResultSubscriber<LocateInfo>() {

                                @Override
                                protected void _onError(Throwable e) {
                                    LogUtils.e(e.getMessage());
                                    //test
                                    mPanel.updateLog(e.getMessage());
                                }

                                @Override
                                protected void _onSuccess(LocateInfo data) {
                                    if (data != null) {
                                        LogUtils.d("we are in (" + data.x + "," + data.y + "," + data.floor + ") at "
                                                + System.currentTimeMillis());
                                        //test debug
                                        mPanel.updateLog(data.x + "," + data.y + "," + data.floor);

                                        // 切换楼层
                                        if(currentFloor != data.floor) {
                                            currentFloor = data.floor;
                                            int pos = computePositionByFloor(currentFloor);
                                            LogUtils.i("switch to floor " + currentFloor + " with position " + pos);
                                            mPager.setCurrentItem(pos, false);
                                            PlanView plan = (PlanView) mPager.findViewWithTag(pos);
                                            currentPlanLayer = (TraceLayer) plan.getLayer("trace");
                                        }

                                        currentPlanLayer.drawTracePoint(data.x, data.y);
                                    }
                                }
                            });
                }
            }
        });
    }

    private int computePositionByFloor(int floor) {
        // an implementation
//        int compansation = (floor ^ minFloor) >= 0 ? 0 : 1;
//        return  (floorCount - 1) - (floor - minFloor) + compansation;

        // an optimization
        int compansation = (floor ^ minFloor) >= 0 ? -1 : 0;
        return  computeFloor - floor + compansation;
    }

    private void simulate() {
        // todo remove me while release

        // we simulate floor switching
        currentFloor = 0;
        final Random randomFloor = new Random(6);
        final Random randomInterval = new Random(10);
        Handler h1 = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
//                mPager.setCurrentItem(curPos % 6);
                currentFloor = randomFloor.nextInt(6) + minFloor;
                if(currentFloor != 0) {
                    int pos = computePositionByFloor(currentFloor);
                    LogUtils.i("floor " + currentFloor + "'s position is " + pos);
                    mPager.setCurrentItem(pos, false);
                    currentPlanLayer = (TraceLayer) ((PlanView)mPager.findViewWithTag(pos)).getLayer("trace");
                }
                sendEmptyMessageDelayed(9, randomInterval.nextInt(20) * 1000);
            }
        };
        h1.sendEmptyMessage(9);

        // we simulate locating
        final Random randomX = new Random(1000);
        final Random randomY = new Random(1001);
        Handler h2 = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                currentPlanLayer.drawTracePoint(randomX.nextFloat() * 200, randomY.nextFloat() * 100);
                sendEmptyMessageDelayed(9, 5000);

            }
        };
        h2.sendEmptyMessage(9);
    }
}
