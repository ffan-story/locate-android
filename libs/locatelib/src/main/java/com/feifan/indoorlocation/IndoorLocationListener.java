package com.feifan.indoorlocation;

import com.feifan.indoorlocation.model.Beacon;
import com.feifan.indoorlocation.model.IndoorLocationModel;

import java.util.List;

/**
 * Created by yangrenyong on 2016/11/2.
 */

public interface IndoorLocationListener {
    void onLocationSucceeded(IIndoorLocator locator, IndoorLocationModel location, List<Beacon> beacons); // 定位成功，返回定位数据和beacon列表

    void onLocationFailed(IIndoorLocator locator, IndoorLocationError error, List<Beacon> beacons); // 定位失败，返回错误码，错误信息和beacon列表

    void onFirstLoadFinished(); // 首次初始化完成
}
