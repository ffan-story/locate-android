package com.feifan.sensorlib.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;

import com.feifan.sensorlib.SensorController;

import java.lang.ref.WeakReference;

/**
 * 传感器服务
 * <p>
 *     采集系统的传感器数据
 * </p>
 * Created by xuchunlei on 16/10/12.
 */

public class SensorService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
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

        }
    }

    /*------------END-------------*/
}
