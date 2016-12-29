package com.feifan.debuglib.window;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.hannesdorfmann.debugoverlay.DebugOverlay;

import com.feifan.debuglib.window.DebugService.DebugBinder;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by xuchunlei on 2016/12/2.
 */

public class DebugWindow {
    private static final String TAG = "DebugWindow";
    private static final DebugWindow INSTANCE = new DebugWindow();
    private boolean mFlag = false;

    // log
    private LogDispatcher mDispatcher;

//    private WeakReference<Context> mContext;

//    private boolean enabled = true;

//    private Handler mHandler;
//    private volatile String mContent;

    // service
    private DebugService mService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DebugBinder binder = (DebugBinder)service;
            mService = binder.getService();
            mDispatcher.setService(mService);
            Log.i(TAG, "start debug service successfully");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    private DebugWindow() {
        mDispatcher = new LogDispatcher();

//        mHandler = new Handler(Looper.getMainLooper()){
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                DebugOverlay.with(mContext.get()).log(mContent);
//            }
//        };
    }

    public void initialize(Context context) {
        if(!mFlag) {
            Intent intent = new Intent(context.getApplicationContext(), DebugService.class);
            context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            mFlag = true;
        }else {
            Log.w(TAG, "debug service was already started");
        }
//        if(mContext == null) {
//            mContext = new WeakReference<Context>(context.getApplicationContext());
//        }
    }

    public void destory(Context context) {
        context.unbindService(mConnection);
        mFlag = false;
    }

    public static DebugWindow get() {
        return INSTANCE;
    }

    public void logI(String content) {
        mDispatcher.enqueueMessage("I:" + content);
    }
    public void logE(String content) {
        mDispatcher.enqueueMessage("E:" + content);
    }

//    public void setEnabled(boolean enabled) {
//        this.enabled = enabled;
//    }

    /**
     * 调试日志分发器
     */
    private static class LogDispatcher {
        private DebugService mService;

        private Queue<String> mLogQueue = new LinkedList<>();

        public void setService(@NonNull DebugService service) {
            mService = service;
            if(!mLogQueue.isEmpty()) {
                for(String log : mLogQueue) {
                    dispatch(log);
                }
                mLogQueue.clear();
            }
        }

        public void enqueueMessage(@NonNull String msg) {
            if (mService != null) {
                dispatch(msg);
            } else {
                mLogQueue.add(msg);
            }
        }

        private void dispatch(String log) {
            String type = log.substring(0, 1);
            if("I".equalsIgnoreCase(type)) {
                mService.logInfo(log.substring(2, log.length()));
            }else if("E".equalsIgnoreCase(type)) {
                mService.logError(log.substring(2, log.length()));
            }

        }
    }
}
