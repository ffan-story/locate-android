package com.feifan.debuglib;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.hannesdorfmann.debugoverlay.DebugOverlay;

import java.lang.ref.WeakReference;

/**
 * Created by xuchunlei on 2016/12/2.
 */

public class DebugWindow {
    private static final DebugWindow INSTANCE = new DebugWindow();
    private WeakReference<Context> mContext;

    private boolean enabled = true;

    private Handler mHandler;
    private volatile String mContent;

    private DebugWindow() {
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                DebugOverlay.with(mContext.get()).log(mContent);
            }
        };
    }

    public void initialize(Context context) {
        if(mContext == null) {
            mContext = new WeakReference<Context>(context.getApplicationContext());
        }
    }

    public static DebugWindow get() {
        return INSTANCE;
    }

    public void log(String content) {
        if(enabled) {
            mContent = content;
            mHandler.sendEmptyMessage(0);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
