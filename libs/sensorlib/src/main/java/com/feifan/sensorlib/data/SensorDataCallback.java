package com.feifan.sensorlib.data;

/**
 * Created by xuchunlei on 2016/12/13.
 */

public interface SensorDataCallback {

    /**
     * 返回传感器数据
     * @param timeStamp 时间戳
     * @param data 传感器数据
     */
    void onDataChanged(long timeStamp, SensorData data);

}
