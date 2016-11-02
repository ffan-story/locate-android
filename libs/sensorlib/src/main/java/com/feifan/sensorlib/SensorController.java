package com.feifan.sensorlib;

import android.content.Context;
import android.hardware.SensorManager;

import com.feifan.baselib.utils.LogUtils;

/**
 * 传感器
 * Created by xuchunlei on 16/10/12.
 */

public class SensorController {

    private static SensorController INSTANCE = null;

    private SensorManager mSensorManager;

    private SensorController(Context context) {
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
    }

    public static SensorController getInstance(Context context) {
        if (INSTANCE == null) {
            LogUtils.d("create a SensorController instance");
            INSTANCE = new SensorController(context);
        }
        return INSTANCE;
    }
}
