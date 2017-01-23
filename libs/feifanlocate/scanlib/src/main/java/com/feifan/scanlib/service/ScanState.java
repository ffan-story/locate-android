package com.feifan.scanlib.service;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.scanlib.beacon.BeaconUtils;
import com.feifan.scanlib.beacon.RawBeacon;
import com.feifan.scanlib.beacon.SampleBeacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 扫描状态,保存扫描状态数据
 * Created by bianying on 16/9/4.
 */
public class ScanState {
//    private Map<RawBeacon, SampleBeacon> mBeacons = new HashMap<>();
    private List<RawBeacon> mBeacons = new ArrayList<>();
    private Map<String, RawBeacon> mMacBeacons = new HashMap<>();

    public void addBeacon(RawBeacon beacon) {
        if(BeaconUtils.isBeacon(beacon.rawData) && !mMacBeacons.containsKey(beacon.mac)) {
            mMacBeacons.put(beacon.mac, beacon);
        }else {
            beacon.fake = true;
        }
        mBeacons.add(beacon);
    }

    public synchronized Collection<SampleBeacon> finalizeBeacons() {
        ArrayList<SampleBeacon> finalizedBeacons = new ArrayList<>();
        synchronized (mBeacons) {
            final double time = System.currentTimeMillis() / 1000d;
            for (RawBeacon beacon : mBeacons) {
                SampleBeacon sampleBeacon = new SampleBeacon(beacon);
                if(beacon.fake) {   // 需要伪造的beacon,需要替换为正确的uuid、major和minor
                    RawBeacon standBeacon = mMacBeacons.get(beacon.mac);
                    if(standBeacon != null) {
                        sampleBeacon.uuid = standBeacon.uuid;
                        sampleBeacon.major = standBeacon.major;
                        sampleBeacon.minor = standBeacon.minor;
                    }
                }
                sampleBeacon.time = time;
                finalizedBeacons.add(sampleBeacon);
            }
            mBeacons.clear();
        }

        return finalizedBeacons;
    }

}
