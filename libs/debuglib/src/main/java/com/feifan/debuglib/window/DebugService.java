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
        mView.show();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mView.hide();
        return super.onUnbind(intent);
    }

    public void logInfo(String log) {
        mView.addLog(DebugView.LOG_LEVEL_INFO, log);
    }
    public void logError(String log) {
        mView.addLog(DebugView.LOG_LEVEL_ERROR, log);
    }

    public class DebugBinder extends Binder {

        public DebugService getService() {
            return DebugService.this;
        }
    }
}
