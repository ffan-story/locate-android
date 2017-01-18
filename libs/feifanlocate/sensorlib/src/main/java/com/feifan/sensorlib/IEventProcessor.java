package com.feifan.sensorlib;

import android.hardware.SensorEvent;

/**
 * Created by xuchunlei on 2016/12/8.
 */

public interface IEventProcessor {

    /**
     * 处理传感器事件及其数据
     * @param event
     */
    void onHandleEvent(SensorEvent event);
}
