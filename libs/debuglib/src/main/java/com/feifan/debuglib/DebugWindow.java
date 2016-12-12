package com.feifan.debuglib;

import android.content.Context;

import com.hannesdorfmann.debugoverlay.DebugOverlay;

import java.lang.ref.WeakReference;

/**
 * Created by xuchunlei on 2016/12/2.
 */

public class DebugWindow {
    private static final DebugWindow INSTANCE = new DebugWindow();
    private WeakReference<Context> mContext;

    private boolean enabled = false;

    private DebugWindow() {

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
            DebugOverlay.with(mContext.get()).log(content);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
