package com.feifan.scanlib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.scanlib.service.CommandData;
import com.feifan.scanlib.service.ScanService;

/**
 * Created by bianying on 16/9/3.
 */
public class ScanManager {

    private static ScanManager client = null;
    private BeaconNotifier mNotifier;

    private ScanManager() {

    }

    /**
     * 获取扫描实例
     * @return
     */
    public static ScanManager getInstance() {
        if (client == null) {
            LogUtils.d("create a ScanManager instance");
            client = new ScanManager();
        }
        return client;
    }

    public void start(String packageName, int period) {
        if (android.os.Build.VERSION.SDK_INT < 18) {
            LogUtils.w("Not supported prior to API 18.  Method invocation will be ignored");
            return;
        }
        Message msg = Message.obtain(null, ScanService.MSG_START_SCAN, 0, 0);
        CommandData obj = new CommandData(packageName, period);
        msg.getData().putParcelable("data", obj);
        try {
            serviceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (android.os.Build.VERSION.SDK_INT < 18) {
            LogUtils.w("Not supported prior to API 18.  Method invocation will be ignored");
            return;
        }

        Message msg = Message.obtain(null, ScanService.MSG_STOP_SCAN, 0, 0);
        try {
            serviceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /*-----------服务-----------*/

    // 连接扫描服务
    private Messenger serviceMessenger = null;
    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.i("scan:connect to scan service");
            serviceMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.i("scan:disconnect to scan service");
            serviceMessenger = null;
        }
    };

    public void bind(Context context){
        if (android.os.Build.VERSION.SDK_INT < 18) {
            LogUtils.w("Not supported prior to API 18.  Method invocation will be ignored");
            return;
        }
        Intent intent = new Intent(context.getApplicationContext(), ScanService.class);
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        LogUtils.d("bind to scan service");
    }

    public void unBind(Context context) {
        if (android.os.Build.VERSION.SDK_INT < 18) {
            LogUtils.w("Not supported prior to API 18.  Method invocation will be ignored");
            return;
        }
        context.unbindService(mConnection);
    }

    /*----------END-----------*/

    public void setNotifier(BeaconNotifier notifier) {
        mNotifier = notifier;
    }

    public BeaconNotifier getNotifier() {
        return mNotifier;
    }
}
