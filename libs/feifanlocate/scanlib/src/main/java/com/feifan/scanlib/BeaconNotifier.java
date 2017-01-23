package com.feifan.scanlib;

import com.feifan.scanlib.beacon.SampleBeacon;

import java.util.Collection;

/**
 * beacon数据监控接口
 *
 * Created by bianying on 16/9/4.
 */
public interface BeaconNotifier {

    void onBeaconsReceived(Collection<SampleBeacon> beacons);
}
