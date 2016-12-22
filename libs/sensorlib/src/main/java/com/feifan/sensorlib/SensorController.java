package com.feifan.sensorlib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.sensorlib.data.SensorDataCallback;
import com.feifan.sensorlib.service.SensorService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 传感器
 * Created by xuchunlei on 16/10/12.
 */

public class SensorController {

    private static SensorController INSTANCE = null;

    private SensorDataCallback mCallback;

    private LinkedList<Message> mPendingQueue = new LinkedList<>();

    private SensorController() {

    }

    public static SensorController getInstance() {
        if (INSTANCE == null) {
            LogUtils.d("create a SensorController instance");
            INSTANCE = new SensorController();
        }
        return INSTANCE;
    }

    public void enableSensor(int sensorType, int frequency) {
        Message msg = Message.obtain();
        msg.what = SensorService.getEnablemsgType(sensorType);
        msg.arg1 = frequency;
        sendMsgQuitely(msg);
    }

    public void disableSensor(int sensorType) {
        Message msg = Message.obtain();
        msg.what = SensorService.getDisablemsgType(sensorType);
        sendMsgQuitely(msg);
    }

    /**
     * 设置扫描频率，单位：次／秒
     * @param frequency
     */
    public void setFrequency(int frequency) {
        Message msg = Message.obtain();
        msg.what = SensorService.MSG_CHANGE_FREQUENCY;
        msg.arg1 = frequency;
        sendMsgQuitely(msg);
    }

    public void setCallPackageName(String pkgName) {
        Message msg = Message.obtain();
        msg.what = SensorService.MSG_SET_CALL_PACKAGE_NAME;
        Bundle bundle = new Bundle();
        bundle.putString("package_name", pkgName);
        msg.setData(bundle);
        sendMsgQuitely(msg);
    }

    private void sendMsgQuitely(Message msg) {
        try {
            serviceMessenger.send(msg);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPendingQueue.offerLast(msg);
    }

    /*-----------服务-----------*/

    // 连接扫描服务
    private Messenger serviceMessenger = null;
    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.i("sensor:connect to sensor service");
            serviceMessenger = new Messenger(service);
            Message msg;
            while((msg = mPendingQueue.pollFirst()) != null) {
                sendMsgQuitely(msg);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.i("sensor:disconnect to sensor service");
            serviceMessenger = null;
        }
    };

    public void bind(Context context){

        Intent intent = new Intent(context.getApplicationContext(), SensorService.class);
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        LogUtils.d("bind to sensor service");
    }

    public void unBind(Context context) {
        context.unbindService(mConnection);
        serviceMessenger = null;
    }

    public void setCallback(SensorDataCallback callback) {
        mCallback = callback;
    }

    public SensorDataCallback getCallback() {
        return mCallback;
    }

    /*----------END-----------*/

}
