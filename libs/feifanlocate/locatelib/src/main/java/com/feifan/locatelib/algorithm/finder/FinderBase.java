package com.feifan.locatelib.algorithm.finder;

import android.content.Context;
import android.hardware.Sensor;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locatelib.algorithm.ILocationFinder;
import com.feifan.locatelib.algorithm.ILocationInspector;
import com.feifan.locatelib.cache.FingerprintStore.FPLocation;
import com.feifan.locatelib.cache.FingerprintStore.FPFeature;
import com.feifan.locatelib.cache.StoreUtils;
import com.feifan.locatelib.inertial.Predictor;
import com.feifan.scanlib.beacon.SampleBeacon;
import com.feifan.sensorlib.SensorController;
import com.feifan.sensorlib.data.SensorData;
import com.feifan.sensorlib.data.SensorDataCallback;

import java.util.List;
import java.util.Map;

/**
 * Created by xuchunlei on 2016/12/1.
 */

public abstract class FinderBase implements ILocationFinder, SensorDataCallback {

    protected ILocationInspector mInspector;

    // store
    protected FPLocation[] mFPLocations;
    protected Map<String, Integer> mBeaconMap;

    // data
    protected int featureCount = 0;
    protected byte[] featureSrc;    // 用于重置定位特征向量
    protected byte[] featureCache;  // 定位特征向量

    // 惯性补偿
    private SensorController mController = SensorController.getInstance();
    protected Predictor mPredictor = new Predictor();

    @Override
    public void initialize(Map<String, Integer> beaconMap, ILocationInspector inspector) {
        mBeaconMap = beaconMap;
        mInspector = inspector;

        // 初始化缓冲

        featureCount = beaconMap.size();
        featureCache = new byte[featureCount];

        featureSrc = new byte[featureCount];
        for(int i = 0;i < featureCount;i++) {
            featureSrc[i] = StoreUtils.NO_SIGNAL;
        }
    }

    @Override
    public void startCompensate(Context context) {
        mController.bind(context);
        mController.setCallback(this);
        mController.setCallPackageName(context.getPackageName());
        // 启动传感器
        mController.enableSensor(Sensor.TYPE_ACCELEROMETER, 10);
//        mController.enableSensor(Sensor.TYPE_MAGNETIC_FIELD, 10);
        mController.enableSensor(Sensor.TYPE_ORIENTATION, 10);
    }

    @Override
    public void stopCompensate(Context context) {
        mController.disableSensor(Sensor.TYPE_ACCELEROMETER);
//        mController.disableSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mController.disableSensor(Sensor.TYPE_ORIENTATION);
        mController.unBind(context);
    }

    @Override
    public void updateFingerprints(FPLocation[] fps) {
        mFPLocations = fps;
    }

    @Override
    public void onDataChanged(long timeStamp, SensorData data) {
        mPredictor.computeStep(data.acceleration.x,
                data.acceleration.y,
                data.acceleration.z,
                data.orientation.azimuth);
//        LogUtils.e(data.orientation.toString());
    }
}
