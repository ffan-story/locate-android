package com.feifan.sensorlib.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

/**
 * Created by xuchunlei on 2016/12/7.
 */

public class SensorDataService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public SensorDataService() {
        super("SensorDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
