package com.feifan.sampling;

import android.app.Application;

import com.feifan.sampling.base.log.GlobalState;
import com.wanda.logger.log.Logger;

/**
 * Created by xuchunlei on 16/5/6.
 */
public class SampleApplication extends Application {
    private boolean isServiceOn = false;
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init("BEACONS",this);
        GlobalState.init(this);
    }

    public boolean isServiceOn() {
        return isServiceOn;
    }

    public void setServiceOn(boolean serviceOn) {
        isServiceOn = serviceOn;
    }
}
