package com.feifan.indoorlocation;

import android.content.Context;

import com.feifan.indoorlocation.model.BeaconDB;

/**
 * Created by yangrenyong on 2016/11/2.
 */

public interface IIndoorLocator {

    void initialize(Context context); // 初始化必要资源

    /**
     * BeaconDB与广场相关，定位时使用的Beacon信息
     * @param beaconDB
     */
    void setBeaconDB(BeaconDB beaconDB);

    BeaconDB getBeaconDB();

    void startUpdatingLocation(IndoorLocationListener listener); //添加listener

    void stopUpdatingLocation(IndoorLocationListener listener); //移除listener


    void destroy(); // 清理Context，listener等

    void setBleScanInterval(long timeInMillis); // 设置BLE扫描间隔，单位ms

    long getBleScanInterval(); // 获得BLE扫描间隔，单位ms

    void setUpdateInterval(long timeInMillis); // 设置定位更新间隔，单位ms

    long getUpdateInterval(); // 获得定位更新间隔，单位ms

    String getToken(); // 返回服务端获取的token，详见备注说明
}

