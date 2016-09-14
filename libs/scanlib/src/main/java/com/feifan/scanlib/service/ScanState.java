package com.feifan.scanlib.service;

import com.feifan.baselib.utils.LogUtils;
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

    public void addBeacon(RawBeacon beacon) {
//        if(mBeacons.containsKey(beacon)) {
//            SampleBeacon sBeacon = mBeacons.get(beacon);
//            sBeacon.update(beacon);
//        }else {
//            mBeacons.put(beacon, new SampleBeacon(beacon));
//        }
        mBeacons.add(beacon);
    }

    public synchronized Collection<SampleBeacon> finalizeBeacons() {
        ArrayList<SampleBeacon> finalizedBeacons = new ArrayList<>();
        synchronized (mBeacons) {
            for (RawBeacon beacon : mBeacons) {
                SampleBeacon sampleBeacon = new SampleBeacon(beacon);
                finalizedBeacons.add(sampleBeacon);
            }
            mBeacons.clear();
        }

//        ArrayList<RawBeacon> pendingBeacons = new ArrayList<>();
//        pendingBeacons.addAll(mBeacons);
//        mBeacons.clear();
//        ArrayList<SampleBeacon> finalizedBeacons = new ArrayList<>();
//        for (RawBeacon beacon : pendingBeacons) {
//            SampleBeacon sampleBeacon = new SampleBeacon(beacon);
//            finalizedBeacons.add(sampleBeacon);
//        }

        return finalizedBeacons;
    }
}
