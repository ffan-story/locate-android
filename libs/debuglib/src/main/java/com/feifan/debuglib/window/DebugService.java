package com.feifan.debuglib.window;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by xuchunlei on 2016/12/27.
 */

public class DebugService extends Service {

    private IBinder mBinder = new DebugBinder();
    private DebugView mView;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if(mView == null) {
            mView = new DebugView(getApplicationContext());
        }
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if(mView.isShown()) {
            mView.hide();
        }

        return super.onUnbind(intent);
    }

    public void enableWindow() {
        mView.show();
    }

    public void disableWindow() {
        mView.hide();
    }

    public void logInfo(String log) {
        _log(DebugView.LOG_LEVEL_INFO, log);
    }
    public void logError(String log) {
        _log(DebugView.LOG_LEVEL_ERROR, log);
    }

    private void _log(int level, String log) {
        if(mView.isShown()) {
            mView.addLog(level, log);
        }else {
            showLogConsole(level, log);
        }
    }

    private void showLogConsole(int level, String log) {
        switch (level) {
            case DebugView.LOG_LEVEL_VERBOSE:
                break;
            case DebugView.LOG_LEVEL_DEBUG:
                break;
            case DebugView.LOG_LEVEL_INFO:
                Log.i(getPackageName(), log);
                break;
            case DebugView.LOG_LEVEL_WARN:
                break;
            case DebugView.LOG_LEVEL_ERROR:
                Log.e(getPackageName(), log);
                break;
        }
    }

    public class DebugBinder extends Binder {

        public DebugService getService() {
            return DebugService.this;
        }
    }
}
