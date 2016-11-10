package com.feifan.locatelib;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.indoorlocation.IIndoorLocator;
import com.feifan.indoorlocation.IndoorLocationListener;
import com.feifan.indoorlocation.model.BeaconDB;
import com.feifan.indoorlocation.model.IndoorLocationModel;
import com.feifan.locatelib.data.DataProcessor;
import com.feifan.scanlib.BeaconNotifier;
import com.feifan.scanlib.ScanManager;
import com.feifan.scanlib.beacon.SampleBeacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by xuchunlei on 2016/11/7.
 */

public abstract class LocatorBase implements IIndoorLocator {

    private Context mContext;
    private String mPlazaId = "";

    private boolean mStarted = false;
    private boolean mPendingStart= false;

    // scan
    private ScanManager mScanManager = ScanManager.getInstance();
    private int mScanPeriod = 3000;

    // query
    private Observable<IndoorLocationModel> mQueryEngine;
    private long mQueryPeriod = 3000l;
    private IndoorLocationModel mLocationModel = new IndoorLocationModel();
    private Subscription mQueryScription;

    // data
    private BeaconDB mBeaconDB;

    // callback
    protected List<IndoorLocationListener> mListeners = new ArrayList<>();

    protected abstract void handleScanData(Map<String, Float> data);

    public LocatorBase() {

    }

    @Override
    public void initialize(Context context) {
        mContext = context;
        mScanManager.bind(context);
        mScanManager.setNotifier(new BeaconNotifier() {
            @Override
            public void onBeaconsReceived(Collection<SampleBeacon> beacons) {
                LogUtils.i("we got " + (beacons == null ? 0 : beacons.size()) + " beacon samples");

                Map<String, Float> sData = DataProcessor.processBeaconData(mPlazaId, beacons);
                LogUtils.i("we got " + sData.size() + " valid beacon samples");
                handleScanData(sData);
            }
        });
    }

    @Override
    public void setBleScanInterval(long timeInMillis) {
        mScanPeriod = (int)timeInMillis;
        if(mStarted) { // 开启扫描和定位时，即时生效
            mStarted = !mScanManager.stop();
            if(!mStarted) {
                mStarted = mScanManager.start(mContext.getPackageName(), mScanPeriod);
            }else {
                LogUtils.w("restart scanning failed while changing scan period to " + timeInMillis);
            }

        }
    }

    @Override
    public long getBleScanInterval() {
        return mScanPeriod;
    }

    @Override
    public void setUpdateInterval(long timeInMillis) {
        mQueryPeriod = timeInMillis;
        if(mStarted) { // 开启扫描和定位时，即时生效

        }
    }

    @Override
    public long getUpdateInterval() {
        return mQueryPeriod;
    }

    @Override
    public void startUpdatingLocation(IndoorLocationListener listener) {
        if(listener == null) {
            throw new NullPointerException("listener should not be null");
        }

        if(!mListeners.contains(listener)) {
            mListeners.add(listener);
            LogUtils.i("add a listener to locator");
        }

        if(!mStarted && !mListeners.isEmpty()) {
            if(TextUtils.isEmpty(mPlazaId)) {
                mPendingStart = true;
            }else {
                mStarted = mScanManager.start(mContext.getPackageName(), mScanPeriod);
                if(!mStarted) {
                    mScanManager.setAutoStart(true, mContext.getPackageName(), mScanPeriod);
                }
                startQueryAtInterval(mQueryPeriod);
            }
        }
    }

    @Override
    public void stopUpdatingLocation(IndoorLocationListener listener) {
        if(listener == null) {
            throw new NullPointerException("listener should not be null");
        }
        mListeners.remove(listener);

        if(mStarted && mListeners.isEmpty()) {
            mStarted = !mScanManager.stop();
            stopQuery();
        }
    }

    @Override
    public void setBeaconDB(BeaconDB beaconDB) {
        mBeaconDB = beaconDB;
        LogUtils.i("locate plaza at " + mBeaconDB.plaza.plazaName + " with id(" + mBeaconDB.plaza.plazaId + ")");
    }

    @Override
    public BeaconDB getBeaconDB() {
        return mBeaconDB;
    }

    @Override
    public String getToken() {
        return null;
    }

    @Override
    public void destroy() {
        mPlazaId = "";
        mPendingStart = false;
        mListeners.clear();
        mScanManager.stop();
        mScanManager.unBind(mContext);
        stopQuery();
    }

    protected abstract void updateLocation(final IndoorLocationModel model);

    private void startQueryAtInterval(long interval) {
        if(mQueryEngine == null) {
            final IIndoorLocator locator = this;
            mQueryEngine = Observable.interval(0, interval, TimeUnit.MILLISECONDS)
                    .map(new Func1<Long, IndoorLocationModel>() {
                        @Override
                        public IndoorLocationModel call(Long aLong) {
                            LogUtils.d("update info to location model at " + System.currentTimeMillis());
                            updateLocation(mLocationModel);
                            return mLocationModel;
                        }
                    });
            mQueryScription = mQueryEngine.subscribe(new Action1<IndoorLocationModel>() {
                @Override
                public void call(IndoorLocationModel indoorLocationModel) {
                    LogUtils.i("update location with " + indoorLocationModel.toString());
                    for(IndoorLocationListener listener : mListeners) {
                        listener.onLocationSucceeded(locator, indoorLocationModel, null);
                    }
                }
            });

        }
    }

    private void stopQuery() {
        if(mQueryScription != null && mQueryScription.isUnsubscribed()) {
            mQueryScription.unsubscribe();
        }
    }
}
