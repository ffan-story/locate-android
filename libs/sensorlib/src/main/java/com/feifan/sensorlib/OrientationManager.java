package com.feifan.sensorlib;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.feifan.baselib.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 方向管理
 * <p>
 *     使用地磁传感器接受并解析方向数据，以角度表示
 * </p>
 * Created by xuchunlei on 16/9/18.
 */
public class OrientationManager {

    private static OrientationManager INSTANCE;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;

    private SensorEventListener mEventListener;
    private List<OrientationListener> mListeners = new ArrayList<>();

    private float[] mGravity;
    private float[] mGeomagnetic;

    private float _R[] = new float[9];            // 旋转矩阵缓存
    private float _I[] = new float[9];            // 倾斜矩阵缓存
    private float _O[] = new float[3];            // 方向矩阵

    private OrientationManager() {
        mEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                // 获取加速传感器数据和地磁数据
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                    mGravity = event.values;
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                    mGeomagnetic = event.values;

                if(calculateOrientation(mGravity, mGeomagnetic)) {
                    for(OrientationListener listener : mListeners) {
                        listener.onOrientationChanged(_O[0]);
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    public static OrientationManager getInstance(Context context) {
        if(context != null && INSTANCE == null) {
            INSTANCE = new OrientationManager();
            INSTANCE.mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            INSTANCE.mAccelerometer = INSTANCE.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            INSTANCE.mMagnetometer = INSTANCE.mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            LogUtils.d("create and init an OrientationManager instance");
        }
        return INSTANCE;
    }

    /**
     * 注册
     * @param listener
     */
    public void register(OrientationListener listener) {
        if(mListeners.isEmpty()) {
            mSensorManager.registerListener(mEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
            mSensorManager.registerListener(mEventListener, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
            LogUtils.i("sensorlib:register accelerometer and magnetometer listener");
        }

        mListeners.add(listener);
    }

    /**
     * 反注册
     * @param listener
     */
    public void unRegister(OrientationListener listener) {
        mListeners.remove(listener);
        if(mListeners.isEmpty()) {
            mSensorManager.unregisterListener(mEventListener);
            LogUtils.i("sensorlib:unregister accelerometer and magnetometer listener");
        }
    }

    // 计算方向
    private boolean calculateOrientation(float[] gravity, float[] magnetic){
        if (gravity != null && magnetic != null) {
            boolean success = SensorManager.getRotationMatrix(_R, _I, gravity, magnetic);
            if (success) {
                SensorManager.getOrientation(_R, _O);
                return true;
            }
        }
        return false;
    }

    /**
     * 方向事件监听
     */
    public interface OrientationListener {
        /**
         * 方向变化
         * @param radian
         */
        void onOrientationChanged(float radian);
    }
}
