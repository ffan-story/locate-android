package com.feifan.scanlib.service;

import com.feifan.scanlib.beacon.RawBeacon;
import com.feifan.scanlib.beacon.SampleBeacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 扫描状态,保存扫描状态数据
 * Created by bianying on 16/9/4.
 */
public class ScanState {
    private Map<RawBeacon, SampleBeacon> mBeacons = new HashMap<>();

    public void addBeacon(RawBeacon beacon) {
        if(mBeacons.containsKey(beacon)) {
            SampleBeacon sBeacon = mBeacons.get(beacon);
            sBeacon.update(beacon);
        }else {
            mBeacons.put(beacon, new SampleBeacon(beacon));
        }
    }

    public synchronized Collection<SampleBeacon> finalizeBeacons() {
        ArrayList<SampleBeacon> finalizedBeacons = new ArrayList<>();

        synchronized (mBeacons) {
            for (RawBeacon beacon : mBeacons.keySet()) {
                finalizedBeacons.add(mBeacons.get(beacon));
            }
            mBeacons.clear();
        }

        return finalizedBeacons;
    }
}
