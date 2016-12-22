package com.feifan.sensorlib.processor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import com.feifan.sensorlib.IEventProcessor;

/**
 * Created by xuchunlei on 2016/12/8.
 */

public class OrientationProcessor implements IEventProcessor {
    private static final OrientationProcessor INSTANCE = new OrientationProcessor();

    // raw data
    private float[] mGravity;
    private float[] mGeomagnetic;

    // process data
    private float _R[] = new float[9];            // 旋转矩阵缓存
    private float _I[] = new float[9];            // 倾斜矩阵缓存
    private float _O[] = new float[3];            // 方向矩阵

    // listener
    private OrientationListener mListener;

    private OrientationProcessor() {

    }

    public static OrientationProcessor getInstance() {
        return INSTANCE;
    }

    @Override
    public void onHandleEvent(SensorEvent event) {
        // 获取加速传感器数据和地磁数据
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;

        if(calculateOrientation(mGravity, mGeomagnetic)) {
            if(mListener != null) {
                mListener.onOrientationChanged(_O);
            }
        }
    }

    public void setListener(OrientationListener listener) {
        mListener = listener;
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
}
