package com.feifan.sensorlib.service;

import android.app.Service;
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
import com.feifan.sensorlib.SensorController;
import com.feifan.sensorlib.process.Exporter;
import com.feifan.sensorlib.process.OrientationListener;
import com.feifan.sensorlib.process.OrientationProcessor;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
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

    // sensor
    private SensorManager mSensorManager;
    private Map<Integer, Sensor> mSensorMap = new ConcurrentHashMap<>();

    // data
    private float mRadian = -1;
    private Exporter mExporter = new Exporter();

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        OrientationProcessor.getInstance().setListener(new OrientationListener() {
            @Override
            public void onOrientationChanged(float radian) {
                mRadian = radian;
            }
        });

        String path = getExternalCacheDir().getAbsolutePath().concat(File.separator);
        mExporter.open(path + "sensor-" + System.currentTimeMillis());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mExporter.close();
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
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            LogUtils.e(event.values[0] + "," + event.values[1] + "," + event.values[2] + "," + mRadian);
//            mExporter.writeLine(System.currentTimeMillis() + "," + event.values[0] + "," + event.values[1] + "," + event.values[2] + "," + mRadian);
        }
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
