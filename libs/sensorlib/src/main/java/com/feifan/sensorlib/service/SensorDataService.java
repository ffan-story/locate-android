package com.feifan.sensorlib.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.debuglib.DebugWindow;
import com.feifan.sensorlib.SensorController;
import com.feifan.sensorlib.data.SensorData;

/**
 * Created by xuchunlei on 2016/12/7.
 */

public class SensorDataService extends IntentService {

    private SensorController mController = SensorController.getInstance();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public SensorDataService() {
        super("SensorDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SensorData data = intent.getParcelableExtra("data");
        if(mController.getCallback() != null) {
            mController.getCallback().onDataChanged(System.currentTimeMillis(), data);
//            DebugWindow.get().log("方向角--> " + data.orientation.azimuth);
        }else {
            LogUtils.w("sensor data callback is null");
        }

    }
}
