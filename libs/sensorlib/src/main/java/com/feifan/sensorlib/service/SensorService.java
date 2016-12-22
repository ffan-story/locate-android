package com.feifan.sensorlib.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.debuglib.DebugWindow;
import com.feifan.sensorlib.SensorController;
import com.feifan.sensorlib.data.SensorData;
import com.feifan.sensorlib.processor.Exporter;
import com.feifan.sensorlib.processor.OrientationListener;
import com.feifan.sensorlib.processor.OrientationProcessor;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 传感器服务
 * <p>
 *     采集系统的传感器数据
 * </p>
 * Created by xuchunlei on 16/10/12.
 */

public class SensorService extends Service implements SensorEventListener {

    public static final int MSG_ENABLE_ACCELEROMETER = 1;
    public static final int MSG_DISABLE_ACCELEROMETER = 2;
    public static final int MSG_ENABLE_LINEAR_ACCELEROMETER = 3;
    public static final int MSG_DISABLE_LINEAR_ACCELEROMETER = 4;
    public static final int MSG_ENABLE_GRAVIMETER = 5;
    public static final int MSG_DISABLE_GRAVIMETER = 6;
    public static final int MSG_ENABLE_MAGNETOMETER = 7;
    public static final int MSG_DISABLE_MAGNETOMETER = 8;
    public static final int MSG_CHANGE_FREQUENCY = 9;
    public static final int MSG_SET_CALL_PACKAGE_NAME = 10;

    // sensor
    private SensorManager mSensorManager;
    private Map<Integer, Sensor> mSensorMap = new ConcurrentHashMap<>();

    // data
    private SensorData mData = new SensorData();
    private Intent mIntent = new Intent();
//    private int flag = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        OrientationProcessor.getInstance().setListener(new OrientationListener() {
            @Override
            public void onOrientationChanged(float[] radian) {
                mData.orientation.azimuth = radian[0];
                mData.orientation.pitch = radian[1];
                mData.orientation.roll = radian[2];
//                flag |= 0x4;
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e("SensorService destroyed");
    }

    /*---------进程间通讯----------*/

    final Messenger mMessenger = new Messenger(new IncomingHandler(this));

    static class IncomingHandler extends Handler {
        private final WeakReference<SensorService> mService;

        public IncomingHandler(SensorService service) {
            mService = new WeakReference<SensorService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SensorService service = mService.get();
            if(service != null) {
                switch (msg.what) {
                    case MSG_SET_CALL_PACKAGE_NAME:
                        String pkgName = msg.getData().getString("package_name");
                        service.mIntent.setComponent(new ComponentName(pkgName, "com.feifan.sensorlib.service.SensorDataService"));
                        break;
                    case MSG_ENABLE_ACCELEROMETER:
                        service.enableAccelerometer(msg.arg1);
                        break;
                    case MSG_DISABLE_ACCELEROMETER:
                        service.disableAccelerometer();
                        break;
                    case MSG_ENABLE_LINEAR_ACCELEROMETER:
                        service.enableLinearAccelerometer(msg.arg1);
                        break;
                    case MSG_DISABLE_LINEAR_ACCELEROMETER:
                        service.disableLinearAccelerometer();
                        break;
                    case MSG_ENABLE_MAGNETOMETER:
                        service.enableMagnetometer(msg.arg1);
                        break;
                    case MSG_DISABLE_MAGNETOMETER:
                        service.disableMagnetometer();
                        break;
                    case MSG_CHANGE_FREQUENCY:
                        service.changeFrequency(msg.arg1);
                        break;
                    default:
                        throw new UnsupportedOperationException("operation " + msg.what + " not supported by sensor service");
                }
            }

        }
    }

    /*------------END-------------*/

    @Override
    public void onSensorChanged(SensorEvent event) {
        OrientationProcessor.getInstance().onHandleEvent(event);

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { // 加速传感器作为变更索引
            mData.acceleration.x = event.values[0];
            mData.acceleration.y = event.values[1];
            mData.acceleration.z = event.values[2];
//            flag |= 0x1;
            mIntent.putExtra("data", mData);
            startService(mIntent);
        }else if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            mData.linearAcceleration.x = event.values[0];
            mData.linearAcceleration.y = event.values[1];
            mData.linearAcceleration.z = event.values[2];
//            flag |= 0x2;
        }
//        if(flag == 0x7) {
//            mIntent.putExtra("data", mData);
//            startService(mIntent);
//            flag = 0;
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static int getEnablemsgType(int sensorType) {
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                return MSG_ENABLE_ACCELEROMETER;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                return MSG_ENABLE_LINEAR_ACCELEROMETER;
            case Sensor.TYPE_MAGNETIC_FIELD:
                return MSG_ENABLE_MAGNETOMETER;
            default:
                return 0;
        }
    }

    public static int getDisablemsgType(int sensorType) {
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                return MSG_DISABLE_ACCELEROMETER;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                return MSG_DISABLE_LINEAR_ACCELEROMETER;
            case Sensor.TYPE_MAGNETIC_FIELD:
                return MSG_DISABLE_MAGNETOMETER;
            default:
                return 0;
        }
    }

    private void changeFrequency(int frequency) {
        int period = 1000000 / frequency;

        // 重启传感器
        for(Map.Entry<Integer, Sensor> entry : mSensorMap.entrySet()) {
            _disableSensor(entry.getKey(), this);
            _enableSensor(entry.getKey(), this, period);
        }
    }

    private void enableAccelerometer(int period) {
        _enableSensor(Sensor.TYPE_ACCELEROMETER, this, period);
    }

    private void disableAccelerometer() {
        _disableSensor(Sensor.TYPE_ACCELEROMETER, this);
    }

    private void enableLinearAccelerometer(int period) {
        _enableSensor(Sensor.TYPE_LINEAR_ACCELERATION, this, period);
    }

    private void disableLinearAccelerometer() {
        _disableSensor(Sensor.TYPE_LINEAR_ACCELERATION, this);
    }

    private void enableMagnetometer(int period) {
        _enableSensor(Sensor.TYPE_MAGNETIC_FIELD, this, period);
    }

    private void disableMagnetometer() {
        _disableSensor(Sensor.TYPE_MAGNETIC_FIELD, this);
    }

    private void _enableSensor(int sensorType, SensorEventListener listener, int frequency) {
        int period = 1000000 / frequency;
        Sensor sensor = mSensorManager.getDefaultSensor(sensorType);
        if(sensor != null) {
            mSensorManager.registerListener(listener, sensor, period);
            mSensorMap.put(sensorType, sensor);
            LogUtils.i("enable sensor " + sensorType + " successfully");
        }else {
            LogUtils.w("sensor " + sensorType + " not supported");
        }

    }

    private void _disableSensor(int sensorType, SensorEventListener listener) {
        if(mSensorMap.containsKey(sensorType)) {
            mSensorManager.unregisterListener(listener, mSensorMap.get(sensorType));
            mSensorMap.remove(sensorType);
            LogUtils.i("disable sensor " + sensorType + " successfully");
        }

    }
}
