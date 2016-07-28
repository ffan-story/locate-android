package com.libs.base.sensor.dici;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Administrator on 2016/1/20.
 */
public class DiciService implements SensorEventListener {
    private Context mContext;
    private static DiciService mDiciService;
    private SensorManager mSensorManager;
    private float[] mInR = new float[9];
    private float[] mInclineMatrix = new float[9];
    private float[] mPrefValues = new float[3];
    private ReadWriteLock mReadWriteLock;
    private float[] mAccelValues = new float[3], mCompassValues = new float[3],mOrientValues = new float[3];
    private double mInclination;
    private int count = 1;
    private float mAzimuth;
    private List<OnSensorCallBack> mList = new ArrayList<OnSensorCallBack>();

    public static DiciService getInstance(Context context){
        if (mDiciService == null){
            synchronized (DiciService.class){
                if(mDiciService == null){
                    mDiciService = new DiciService(context);
                }
            }
        }
        return mDiciService;
    }

    private DiciService(Context context){
        if(context != null){
            mContext = context;
            mReadWriteLock = new ReentrantReadWriteLock();
            mSensorManager =  (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
        }
    }

    public void startMagicScan(){
        mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME );
        mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME );
        mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_GAME );


    }

    public void stopMagicScan(){
        if (mList == null || mList.isEmpty()) {
            mSensorManager.unregisterListener(this,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
            mSensorManager.unregisterListener(this,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
            mSensorManager.unregisterListener(this,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //【1】将相关传感器的数值分别读入accelValues，compassValues（磁力感应器的数值）和orientValues数组中
        switch(event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                for(int i = 0 ; i < 3 ; i ++){
                    mAccelValues[i] = event.values[i];
                }
                if(mCompassValues[0] != 0) //如果accelerator和magnetic传感器都有数值，设置为真
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                for(int i = 0 ; i < 3 ; i ++){
                    mCompassValues[i] = event.values[i];
                }
                if(mAccelValues[2] != 0) //检查accelerator和magnetic传感器都有数值，只是换一个轴向检查
                break;

            case Sensor.TYPE_ORIENTATION:
                for(int i = 0 ; i < 3 ; i ++){
                    mOrientValues[i] = event.values[i];
                }
                break;
        }

        //【2】根据加速传感器的数值accelValues[3]和磁力感应器的数值compassValues[3]，进行矩阵计算，获得方位
        //【2.1】计算rotation matrix R(inR)和inclination matrix I(inclineMatrix)
        if(SensorManager.getRotationMatrix(mInR, mInclineMatrix, mAccelValues, mCompassValues)){
            /* 【2.2】根据rotation matrix计算设备的方位。，范围数组：
            values[0]: azimuth, rotation around the Z axis.
            values[1]: pitch, rotation around the X axis.
            values[2]: roll, rotation around the Y axis.*/
            SensorManager.getOrientation(mInR, mPrefValues);
            //【2.2】根据inclination matrix计算磁仰角，地球表面任一点的地磁场总强度的矢量方向与水平面的夹角。
            mInclination = SensorManager.getInclination(mInclineMatrix);

            //【3】显示测量值
            if(count++ % 50 == 0){
                doUpdate();
                count = 1;
            }
        }
    }


    private void doUpdate(){
        mReadWriteLock.writeLock().lock();
        try {
            //preValues[0]是方位角，单位是弧度，范围是-pi到pi，通过Math.toDegrees()转换为角度
            mAzimuth = (float) Math.toDegrees(mPrefValues[0]);
            notifySensorCallBack(mPrefValues);
//        String msg = String.format("推荐方式：\n方位角：%7.3f\npitch: %7.3f\nroll: %7.3f\n地磁仰角：%7.3f\n",
//                mAzimuth,Math.toDegrees(mPrefValues[1]),Math.toDegrees(mPrefValues[2]),
//                Math.toDegrees(mInclination));
//        System.out.println(msg);
        }finally {
            mReadWriteLock.writeLock().unlock();
        }
    }

    /**
     * 获得方向夹角
     * @return
     */
    public float getAzimuth(){
        mReadWriteLock.readLock().lock();
        try {
            return mAzimuth;
        }finally {
            mReadWriteLock.readLock().unlock();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void registerSensorCallBack(OnSensorCallBack callBack){
        if (callBack != null) {
            mList.add(callBack);
        }
    }

    public void unRegisterSensorCallBack(OnSensorCallBack callBack){
        if (callBack != null) {
            mList.remove(callBack);
        }
    }

    public void notifySensorCallBack(float[] prefvalues){
        if (prefvalues != null) {
            if (mList != null && mList.size() > 0) {
                for (int i = 0; i < mList.size(); i++) {
                    OnSensorCallBack callback = mList.get(i);
                    if (callback != null) {
                        callback.onSensorCallBack(prefvalues);
                    }
                }
            }
        }
    }

    public interface OnSensorCallBack{
        public void onSensorCallBack(float[] prefvalues);
    }
}
