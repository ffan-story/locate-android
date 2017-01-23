package com.feifan.locatelib.online;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.indoorlocation.model.IndoorLocationModel;
import com.feifan.locatelib.LocatorBase;
import com.feifan.locatelib.cache.BeaconStore;
import com.feifan.locatelib.cache.FingerprintStore;
import com.feifan.locatelib.cache.FingerprintStore.FPLocation;
import com.feifan.locatelib.cache.LocateInfo;
import com.feifan.locatelib.network.HttpResult;
import com.feifan.locatelib.network.HttpResultSubscriber;
import com.feifan.locatelib.network.TransformUtils;
import com.feifan.scanlib.beacon.SampleBeacon;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Created by xuchunlei on 2016/11/7.
 */

public final class OnLineLocator extends LocatorBase {

    private static final OnLineLocator INSTANCE = new OnLineLocator();


    private transient LocateQueryData mQueryData = new LocateQueryData();
    private RxFingerLocateService mLocateService;

    public static final OnLineLocator getInstance() {
        return INSTANCE;
    }

    @Override
    protected void handleScanData(Collection<SampleBeacon> rawData, Collection<SampleBeacon> data) {
        LogUtils.i("we got " + (data == null ? 0 : data.size()) + " beacon samples");

//                Map<String, Float> sData = DataProcessor.processBeaconData(mPlazaId, beacons);
        Map<String, Float> sData = BeaconStore.getInstance().process2Map(data);
        LogUtils.i("we got " + sData.size() + " valid beacon samples");
        mQueryData.upDateTensor(sData);
//        queryData.upDateTensor(MockServer.TENSOR_DATA_860100010060300001);
    }

    @Override
    protected void updateLocation(final IndoorLocationModel model) {
        if(!mQueryData.tensor.isEmpty()) {
            mLocateService.getLocation(mQueryData)
                    .compose(TransformUtils.<HttpResult<LocateInfo>>defaultSchedulers())
                    .subscribe(new HttpResultSubscriber<LocateInfo>() {

                        @Override
                        protected void _onError(Throwable e) {
                            LogUtils.e(e.getMessage());
                            //test
//                            mPanel.updateLog(e.getMessage());
                            LogUtils.e(e.getMessage());
                        }

                        @Override
                        protected void _onSuccess(LocateInfo data) {
                            if (data != null) {
                                LogUtils.d("we are in (" + data.x + "," + data.y + "," + data.floor + ") at "
                                        + System.currentTimeMillis());
                                //test debug
//                                mPanel.updateLog(data.x + "," + data.y + "," + data.floor);

                                model.x = data.x;
                                model.y = data.y;
                                model.floor = data.floor;
                                model.timestamp = System.currentTimeMillis();
                            }
                        }
                    });
        }
    }
}

    // network
//    private transient LocateQueryData queryData = new LocateQueryData();
//    private Observable<Long> queryEngine;
//    private Subscription querySubscription;
//    private RxFingerLocateService locateService;

//        mScanManager.setAutoStart(true, context.getPackageName(), period);
//        mScanManager.bind(context);
//        mScanManager.setNotifier(new BeaconNotifier() {
//            @Override
//            public void onBeaconsReceived(Collection<SampleBeacon> beacons) {
//                LogUtils.i("we got " + (beacons == null ? 0 : beacons.size()) + " beacon samples");
//                Map<String, Float> sData = DataProcessor.processBeaconData(queryData.position, beacons);
//                LogUtils.i("we got " + sData.size() + " valid beacon samples");
//                queryData.upDateTensor(sData);
////                queryData.upDateTensor(MockServer.TENSOR_DATA_860100010060300001);
//            }
//        });

// query
//        queryData.algorithm = LocatingConfig.getInstance().getAlgorithm();
//        queryData.position = "android_" + mBuilding.code;
////        queryData.position = building.code;
//        String baseUrl = String.format("http://%s:%d", LocatePreferences.getInstance().getLocateAddr(),
//                LocatePreferences.getInstance().getLocatePort());
//        locateService = ServiceFactory.getInstance().createService(RxFingerLocateService.class, baseUrl);

//        mScanManager.unBind(getContext());
//        if(!querySubscription.isUnsubscribed()){
//            querySubscription.unsubscribe();
//        }

//    private void startQueryAtInterval(int interval) {
//        queryEngine = Observable.interval(0, interval, TimeUnit.SECONDS);
//        querySubscription = queryEngine.subscribe(new Action1<Long>() {
//            @Override
//            public void call(Long aLong) {
//                if(!queryData.tensor.isEmpty()) {
//                    locateService.getLocation(queryData)
//                            .compose(TransformUtils.<HttpResult<LocateInfo>>defaultSchedulers())
//                            .subscribe(new HttpResultSubscriber<LocateInfo>() {
//
//                                @Override
//                                protected void _onError(Throwable e) {
//                                    LogUtils.e(e.getMessage());
//                                    //test
//                                    mPanel.updateLog(e.getMessage());
//                                }
//
//                                @Override
//                                protected void _onSuccess(LocateInfo data) {
//                                    if (data != null) {
//                                        LogUtils.d("we are in (" + data.x + "," + data.y + "," + data.floor + ") at "
//                                                + System.currentTimeMillis());
//                                        //test debug
//                                        mPanel.updateLog(data.x + "," + data.y + "," + data.floor);
//
//                                        if(mCurrentFloor != data.floor) {
//                                            mCurrentFloor = data.floor;
//                                            // 切换楼层
//                                            mIndicator.setCurrent(mCurrentFloor);
//                                            LogUtils.i("switch to floor " + mCurrentFloor);
//                                        }
//
//                                        mLocation.a(data.x);
//                                        mLocation.b(data.y);
//                                        mLocation.setFloor(data.floor);
//                                        mMapView.setMyCurrentLocation(mLocation, false);
//
//                                    }
//                                }
//                            });
//                }
//            }
//        });
//    }

//    private void simulate() {
//        final Random rW = new Random(System.currentTimeMillis());
//        final Random rH = new Random(System.currentTimeMillis());
//        final Random rF = new Random(System.currentTimeMillis());
//        Handler h = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                mLocation.a(rW.nextFloat() * 1000);
//                mLocation.b(rH.nextFloat() * 1000);
////                mLocation.setFloor(rF.nextInt(5) - 2);
//                mLocation.setFloor("F1");
//                mIndicator.setCurrent(-1);
//                LogUtils.e("locate to " + mLocation.getX() + "," + mLocation.getY() + " at " + mLocation.getFloor());
//                mMapView.setMyCurrentLocation(mLocation, false);
////                sendEmptyMessageAtTime(9, 2000);
////                sendEmptyMessage(9);
//            }
//        };
////        h.sendEmptyMessage(9);
//        h.sendEmptyMessageDelayed(9, 5000);
//    }

//LogUtils.i("we are locating in " + queryData.position + " now");

//        startQueryAtInterval(Integer.valueOf(LocatingConfig.getInstance().getRequestPeriod()));
//        simulate();

//if(!querySubscription.isUnsubscribed()) {
//        querySubscription.unsubscribe();
//        }
//            startQueryAtInterval(period);

//else if(key.equals(LocatingConfig.KEY_ALGORITHM)) {
//        queryData.algorithm = sharedPreferences.getString(key, queryData.algorithm);
//        }

//LogUtils.i("we do locate with " + queryData.algorithm + " every "
//        + LocatingConfig.getInstance().getRequestPeriod() + "s and scan beacon every "
//        + LocatingConfig.getInstance().getScanPeriod() + "s");