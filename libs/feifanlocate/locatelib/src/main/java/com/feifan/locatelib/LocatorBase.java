package com.feifan.locatelib;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.os.ResultReceiver;
import android.util.JsonReader;
import android.widget.Toast;

import com.feifan.baselib.utils.IOUtils;
import com.feifan.baselib.utils.LogUtils;
import com.feifan.indoorlocation.IIndoorLocator;
import com.feifan.indoorlocation.IndoorLocationListener;
import com.feifan.indoorlocation.model.IndoorLocationModel;
import com.feifan.locatelib.algorithm.ILocationFinder;
import com.feifan.locatelib.algorithm.ILocationInspector;
import com.feifan.locatelib.algorithm.inspector.MinRInspector;
import com.feifan.locatelib.cache.FingerprintStore;
import com.feifan.scanlib.BeaconNotifier;
import com.feifan.scanlib.ScanManager;
import com.feifan.scanlib.beacon.SampleBeacon;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by xuchunlei on 2016/11/7.
 */

public abstract class LocatorBase implements IIndoorLocator {

    private Context mContext;

    private static final int STATUS_NONE = 0;
    private static final int STATUS_READY = STATUS_NONE + 1;
    private static final int STATUS_STARTED = STATUS_READY + 1;
    private static final int STATUS_STOPPED = STATUS_STARTED + 1;
    private static final int STATUS_HANG_UP = STATUS_STOPPED + 1;
    private int mStatus = STATUS_NONE;

    // scan
    private ScanManager mScanManager = ScanManager.getInstance();
    private int mScanPeriod = 3000;
    private LinkedList<SampleBeacon> mSampleCache = new LinkedList<>();
    private LinkedList<Integer> mSampleCount = new LinkedList<>();
    private static final int WINDOW_SIZE = 2;

    // algorithm
    protected ILocationFinder mFinder;
    private ILocationInspector mInspector;

    // query
    private Observable<IndoorLocationModel> mQueryEngine;
    private long mQueryPeriod = 1000l;
    private IndoorLocationModel mLocationModel = new IndoorLocationModel();
    private Subscription mQueryScription;

    // callback
    protected List<IndoorLocationListener> mListeners = new ArrayList<>();
    private BeaconNotifier mNotifier;
    private ResultReceiver plazaReceiver;

    /**
     * 处理原始的beacon数据
     * @param data
     */
    protected abstract void handleScanData(Collection<SampleBeacon> data);

    public LocatorBase() {
        mNotifier = new BeaconNotifier() {
            @Override
            public void onBeaconsReceived(Collection<SampleBeacon> beacons) {

//                handleScanData(beacons);
                // 使用窗口
//                mSampleCount.offerLast(beacons.size());
//                mSampleCache.addAll(beacons);
//                handleScanData(mSampleCache);

                // moving window
//                if(mSampleCount.size() == WINDOW_SIZE) { // 最近三次的采样集合
//                    // 移除最早采集一次的所有样本数据
//                    int count = mSampleCount.pollFirst();
//                    for(int i = 0;i < count;i++) {
//                        mSampleCache.pollFirst();
//                    }
//                }

                // 测试数据
                handleScanData(getBeacons());
            }
        };

        mInspector = new MinRInspector();
//        mInspector = new DefaultInspector();
//        mInspector.setMeasureValue(3f * mQueryPeriod / 1000);
    }

    @Override
    public void initialize(Context context) {
        mContext = context;
        mScanManager.bind(context);
        mScanManager.setNotifier(mNotifier);

        // 初始化广场和楼层
        plazaReceiver = new ResultReceiver(new Handler(Looper.getMainLooper())){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                super.onReceiveResult(resultCode, resultData);
                switch (resultCode) {
                    case PlazaDetectorService.RESULT_CODE_PLAZA:
                        // 更新广场信息
                        String plazaId = resultData.getString(PlazaDetectorService.RESULT_KEY_PLAZA);
                        int floor = resultData.getInt(PlazaDetectorService.RESULT_KEY_PLAZA_FLOOR);

                        // 初始化位置
                        mLocationModel.floor = floor;
                        mLocationModel.locationInfo.plazaId = plazaId;
//                        mLocationModel.locationInfo.plazaName = info.plazaName;
                        notifyListeners(mLocationModel);

                        break;
                    case PlazaDetectorService.RESULT_CODE_STATUS:
                        final int status = resultData.getInt(PlazaDetectorService.RESULT_KEY_STATUS_FLAG);
                        switch (status) { // 指纹库初始化完成
                            case PlazaDetectorService.STATUS_SUCCESS:
                                // 初始化Finder
                                final String beaconFile = resultData.getString(PlazaDetectorService.RESULT_KEY_BEACON_FILE);
                                mFinder.initialize(getBeaconMap(beaconFile), mInspector);
                                mFinder.updateFingerprints(FingerprintStore.getInstance().selectFingerprints(mLocationModel.floor));

                                if(mStatus == STATUS_HANG_UP) { // 挂起状态，则开启
                                    doStart();
                                }
                                break;
                            case PlazaDetectorService.STATUS_ERROR_FPFILE_NOT_FOUND:
                                Toast.makeText(mContext, "fingerprints file not found", Toast.LENGTH_SHORT).show();
                                break;
                            case PlazaDetectorService.STATUS_ERROR_UNKNOWN:
                                Toast.makeText(mContext, "unknown error:maybe occurs while doing io", Toast.LENGTH_SHORT).show();
                                break;
                            case PlazaDetectorService.STATUS_ERROR_PLAZA_NOT_FOUND:
                                Toast.makeText(mContext, "plaza not found", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(mContext, "error from server:" + status, Toast.LENGTH_SHORT).show();
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void setBleScanInterval(long timeInMillis) {
        mScanPeriod = (int)timeInMillis;
        synchronized (this) {
            if(mStatus == STATUS_STARTED) { // 开启扫描和定位时，即时生效
                mStatus = mScanManager.stop() ? STATUS_READY : mStatus;
                if(mStatus == STATUS_READY) {
                    mStatus = mScanManager.start(mContext.getPackageName(), mScanPeriod) ? STATUS_STARTED : STATUS_HANG_UP;
                    LogUtils.d("restart ble scan with period=" + mScanPeriod);
                }else {
                    LogUtils.w("restart scanning failed while changing scan period to " + timeInMillis);
                }
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
        synchronized (this) {
            if(mStatus == STATUS_STARTED) { // 开启扫描和定位时，即时生效
                LogUtils.d("restart interval query with period=" + mQueryPeriod);
//                mInspector.setMeasureValue(3f * mQueryPeriod / 1000);
                stopQuery();
                startQueryAtInterval(mQueryPeriod);
            }
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

        Intent intent = new Intent(mContext, PlazaDetectorService.class);
        intent.putExtra("receiver", plazaReceiver);
        mContext.startService(intent);

        if(!mListeners.contains(listener)) {
            mListeners.add(listener);
            LogUtils.i("add a listener to locator");
        }
        if(mStatus == STATUS_READY) { // 就绪状态则开启
            doStart();
        }else { // 挂起
            mStatus = STATUS_HANG_UP;
        }
        printStatus();

        // 开启补偿
        mFinder.startCompensate(mContext);
    }

    @Override
    public void stopUpdatingLocation(IndoorLocationListener listener) {
        if(listener == null) {
            throw new NullPointerException("listener should not be null");
        }
        mListeners.remove(listener);

        doStop();
    }

    @Override
    public boolean isFirstLoadFinished() {
        // fixme remove it or not
        return false;
    }

    @Override
    public void destroy() {
        mListeners.clear();
        mScanManager.stop();
        mScanManager.unBind(mContext);
        stopQuery();
        mStatus = STATUS_NONE;
        mSampleCache.clear();

        mFinder.stopCompensate(mContext);

        // todo may be reuse?
        mQueryScription = null;
        mQueryEngine = null;
    }

    protected boolean isStarted() {
        return mStatus == STATUS_STARTED;
    }

    protected abstract void updateLocation(final IndoorLocationModel model);

    private void doStart() {
        if(!mListeners.isEmpty()) {
            mStatus = mScanManager.start(mContext.getPackageName(), mScanPeriod) ? STATUS_STARTED : STATUS_HANG_UP;
            if(mStatus == STATUS_STARTED) {
                startQueryAtInterval(mQueryPeriod);
            }

        }

    }

    private void doStop() {
        if(mStatus == STATUS_STARTED || mStatus == STATUS_HANG_UP && mListeners.isEmpty()) {
            mStatus = mScanManager.stop() ? STATUS_READY : mStatus;
            if(mStatus == STATUS_READY) {
                stopQuery();
            }
        }
    }

    private void startQueryAtInterval(long interval) {
        if(mQueryEngine == null) {
            mQueryEngine = Observable.interval(0, interval, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
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
                    notifyListeners(indoorLocationModel);
                }
            });

        }
    }

    private void stopQuery() {
        if(mQueryScription != null && !mQueryScription.isUnsubscribed()) {
            mQueryScription.unsubscribe();
            mQueryEngine = null;
            mQueryScription = null;
        }
    }

    private void notifyListeners(IndoorLocationModel model) {
        for(IndoorLocationListener listener : mListeners) {
            listener.onLocationSucceeded(this, model, null);
        }
    }

    // 简化文件格式可以提升解析速度
    private Map<String, Integer> getBeaconMap(String fileName) {
        Map<String, Integer> resultMap = new TreeMap<>();

        List<String> sortCache = new ArrayList<>(10);
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(fileName));
            reader.beginArray();
            String name;
            int major, minor;
            while(reader.hasNext()) {
                reader.beginObject();
                major = -1;
                minor = -1;
                while(reader.hasNext()) {
                    name = reader.nextName();
                    if("major".equalsIgnoreCase(name)) {
                        major = reader.nextInt();
                    }else if("minor".equalsIgnoreCase(name)) {
                        minor = reader.nextInt();
                    }else {
                        reader.skipValue();
                    }
                }
                sortCache.add(major + "_" + minor);
                reader.endObject();
            }
            reader.endArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(reader);
        }

        // 初始化minorMap
        Collections.sort(sortCache);
        for(int i = 0;i < sortCache.size();i++) {
            String key = sortCache.get(i);
            resultMap.put(key, i);
        }

        return resultMap;
    }

    private void printStatus() {
        LogUtils.d("locator:status to " + mStatus);
    }

    private Collection<SampleBeacon> getBeacons() {
        // {'floor': 1, 'loc_x': 198.42, 'loc_y': -61.91}
        ArrayList<SampleBeacon> samples = new ArrayList<>();
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42493, -76));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42496, -91));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42527, -84));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42539, -88));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42553, -69));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42593, -82));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42597, -70));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42608, -82));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42654, -77));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42658, -81));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42835, -89));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42853, -91));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42859, -88));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42864, -80));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43097, -87));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43106, -77));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43182, -80));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43194, -84));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43201, -86));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43208, -89));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43213, -89));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43225, -65));

        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43289, -88));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43304, -80));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43357, -86));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43387, -90));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43395, -78));
        samples.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43415, -86));

        return samples;
    }

    // 测试用
    public void setFinder(ILocationFinder finder) {
        mFinder = finder;
    }

    public ILocationFinder getFinder() {
        return mFinder;
    }
}
