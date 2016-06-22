package com.mm.beacon;


import com.mm.beacon.blue.ScanData;
import com.mm.beacon.data.IBeacon;
import com.mm.beacon.data.Region;

import java.util.List;

/**
 * Created by mengmeng on 15/8/27.
 */
public interface BeaconDispatcher {
    public void onBeaconDetect(List<IBeacon> beaconlist);
    public void onBeaconRawDataDetect(List<ScanData> beaconlist);
    public void onBeaconEnter(Region region);
    public void onBeaconExit(Region region);
}
