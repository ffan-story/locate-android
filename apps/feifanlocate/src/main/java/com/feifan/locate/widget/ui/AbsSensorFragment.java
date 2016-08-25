package com.feifan.locate.widget.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.feifan.locate.widget.cursorwork.AbsLoaderFragment;

/**
 * Created by xuchunlei on 16/8/24.
 */
public abstract class AbsSensorFragment extends AbsLoaderFragment implements SensorEventListener {

    // sensor，方向传感器使用地磁和加速硬件传感器的数据
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] mGravity;
    private float[] mGeomagnetic;
    private float _R[] = new float[9];            // 旋转矩阵缓存
    private float _I[] = new float[9];            // 倾斜矩阵缓存
    private float _O[] = new float[3];            // 方向矩阵

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this,magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
    // 获取加速传感器数据和地磁数据
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;

        if(calculateOrientation(mGravity, mGeomagnetic)) {
            onOrientationChanged(_R[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected abstract void onOrientationChanged(float radian);

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
