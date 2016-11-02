package com.feifan.locate.locating;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.feifan.locate.Constants;
import com.feifan.locate.LocatePreferences;
import com.feifan.locate.MockServer;
import com.feifan.locate.R;
import com.feifan.locate.common.BuildingModel;
import com.feifan.locate.locating.config.LocatingConfig;
import com.feifan.locate.locating.config.LocatingPanel;
import com.feifan.locate.provider.LocateData.Zone;
import com.feifan.locate.widget.cursorwork.AbsLoaderFragment;
import com.feifan.locate.widget.cursorwork.ICursorAdapter;
import com.feifan.locate.widget.ui.BaseFragment;
import com.feifan.locate.widget.ui.TextIndicator;
import com.feifan.locate.widget.ui.TextIndicator.TextIndicatorModel;
import com.feifan.locatelib.network.HttpResult;
import com.feifan.locatelib.network.HttpResultSubscriber;
import com.feifan.locatelib.network.ServiceFactory;
import com.feifan.locatelib.network.TransformUtils;
import com.feifan.locatelib.online.LocateInfo;
import com.feifan.locatelib.online.LocateQueryData;
import com.feifan.locatelib.online.RxFingerLocateService;
import com.feifan.scanlib.BeaconNotifier;
import com.feifan.scanlib.ScanManager;
import com.feifan.scanlib.beacon.SampleBeacon;
import com.rtm.frm.data.Location;
import com.rtm.frm.map.LocationLayer;
import com.rtm.frm.map.MapView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by xuchunlei on 2016/10/27.
 */

public class RealtimePlanFragment extends AbsLoaderFragment implements OnSharedPreferenceChangeListener {

    @IdRes
    private static final int ID_MENU_SETTING = 1;

    // scan
    private ScanManager mScanManager = ScanManager.getInstance();
//    private int mScanPeriod = Integer.valueOf(LocatingConfig.getInstance().getScanPeriod()) * 1000;

    // network
    private transient LocateQueryData queryData = new LocateQueryData();
    private Observable<Long> queryEngine;
    private Subscription querySubscription;
    private RxFingerLocateService locateService;

    // view
    private LocatingPanel mPanel;
    private MapView mMapView;
    private FloorIndicator mIndicator;

    // locate
    private int mCurrentFloor = -100;
    private Location mLocation = new Location(0, 0);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // scan
        int period = Integer.valueOf(LocatingConfig.getInstance().getScanPeriod()) * 1000;
        mScanManager.setAutoStart(true, context.getPackageName(), period);
        mScanManager.bind(context);
        mScanManager.setNotifier(new BeaconNotifier() {
            @Override
            public void onBeaconsReceived(Collection<SampleBeacon> beacons) {
                LogUtils.i("we got " + (beacons == null ? 0 : beacons.size()) + " beacon samples");
                Map<String, Float> sData = DataProcessor.processBeaconData(queryData.position, beacons);
                LogUtils.i("we got " + sData.size() + " valid beacon samples");
                queryData.upDateTensor(sData);
//                queryData.upDateTensor(MockServer.TENSOR_DATA_860100010060300001);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_realtime_plan, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BuildingModel building = getArguments().getParcelable(Constants.EXTRA_KEY_BUILDING);
        mMapView = findView(R.id.realtime_map);
        Bitmap normal = BitmapFactory.decodeResource(getResources(), R.mipmap.plan_location);
        LocationLayer layer = new LocationLayer(mMapView, normal, normal);
        mMapView.addMapLayer(layer);
        mMapView.setCurrentBuildId(building.code);
        mMapView.initScale();

        mPanel = (LocatingPanel) getLayoutInflater(savedInstanceState).inflate(R.layout.layout_locating_panel,
                null, false);
        mPanel.setConfigChangeListener(this);

        mIndicator = findView(R.id.realtime_indicator);
        mIndicator.setMapView(mMapView, building.code);

        // query
        queryData.algorithm = LocatingConfig.getInstance().getAlgorithm();
        queryData.position = "android_" + building.code;
//        queryData.position = building.code;
        String baseUrl = String.format("http://%s:%d", LocatePreferences.getInstance().getLocateAddr(),
                LocatePreferences.getInstance().getLocatePort());
        locateService = ServiceFactory.getInstance().createService(RxFingerLocateService.class, baseUrl);
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

    private void startQueryAtInterval(int interval) {
        queryEngine = Observable.interval(0, interval, TimeUnit.SECONDS);
        querySubscription = queryEngine.subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                LogUtils.e("called at " + System.currentTimeMillis());
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

                                        if(mCurrentFloor != data.floor) {
                                            mCurrentFloor = data.floor;
                                            // 切换楼层
                                            mIndicator.setCurrent(mCurrentFloor);
                                            LogUtils.i("switch to floor " + mCurrentFloor);
                                        }

                                        mLocation.a(data.x);
                                        mLocation.b(data.y);
                                        mLocation.setFloor(data.floor);
                                        mMapView.setMyCurrentLocation(mLocation, false);

                                    }
                                }
                            });
                }
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(LocatingConfig.KEY_SCAN_PERIOD)) {
            int period = Integer.valueOf(sharedPreferences.getString(key, "3"));
            mScanManager.stop();
            mScanManager.start(getContext().getPackageName(), period * 1000);
        } else if(key.equals(LocatingConfig.KEY_REQUEST_PERIOD)) {
            int period = Integer.valueOf(sharedPreferences.getString(key, "3"));
            if(!querySubscription.isUnsubscribed()) {
                querySubscription.unsubscribe();
            }
            startQueryAtInterval(period);
        } else if(key.equals(LocatingConfig.KEY_ALGORITHM)) {
            queryData.algorithm = sharedPreferences.getString(key, queryData.algorithm);
        }
        LogUtils.i("we do locate with " + queryData.algorithm + " every "
                + LocatingConfig.getInstance().getRequestPeriod() + "s and scan beacon every "
                + LocatingConfig.getInstance().getScanPeriod() + "s");
    }

    private void simulate() {
                final Random rW = new Random(System.currentTimeMillis());
        final Random rH = new Random(System.currentTimeMillis());
        final Random rF = new Random(System.currentTimeMillis());
        Handler h = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mLocation.a(rW.nextFloat() * 1000);
                mLocation.b(rH.nextFloat() * 1000);
//                mLocation.setFloor(rF.nextInt(5) - 2);
                mLocation.setFloor("F1");
                mIndicator.setCurrent(-1);
                LogUtils.e("locate to " + mLocation.getX() + "," + mLocation.getY() + " at " + mLocation.getFloor());
                mMapView.setMyCurrentLocation(mLocation, false);
//                sendEmptyMessageAtTime(9, 2000);
//                sendEmptyMessage(9);
            }
        };
//        h.sendEmptyMessage(9);
        h.sendEmptyMessageDelayed(9, 5000);
    }

    @Override
    protected int getLoaderId() {
        return Constants.LOADER_ID_ZONE;
    }

    @Override
    protected Uri getContentUri() {
        return Zone.CONTENT_URI;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        super.onLoadFinished(loader, data);

        if(data != null && data.getCount() != 0) {
            List<TextIndicatorModel> listData = new ArrayList<>();
            while(data.moveToNext()) {
                int floorNoIndex = data.getColumnIndexOrThrow(Zone.FLOOR_NO);
                int titleIndex = data.getColumnIndexOrThrow(Zone.TITLE);
                listData.add(new TextIndicatorModel(data.getInt(floorNoIndex), data.getString(titleIndex)));
            }
            mIndicator.setData(listData);
            mIndicator.setCurrent(1);
        }
        loader.stopLoading();

        startQueryAtInterval(Integer.valueOf(LocatingConfig.getInstance().getRequestPeriod()));
        LogUtils.i("we are locating in " + queryData.position + " now");

//        simulate();
    }

    @Override
    protected <A extends ICursorAdapter> A getAdapter() {
        return null;
    }
}
