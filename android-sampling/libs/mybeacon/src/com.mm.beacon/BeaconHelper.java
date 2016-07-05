package com.mm.beacon;


import com.mm.beacon.data.FilterBeacon;

import java.util.List;

/**
 * Created by mengmeng on 15/11/3.
 */
public class BeaconHelper {

    public static boolean isBeaconMatched(IBeacon iBeacon, BeaconFilter beaconFilter) {
        if (beaconFilter != null) {
            List<FilterBeacon> beaconList = beaconFilter.getBeaconList();
            if (beaconList == null || beaconList.isEmpty()) {
                return true;
            }
            if (iBeacon != null) {
                if (beaconList != null && !beaconList.isEmpty()) {
                    for (int i = 0; i < beaconList.size(); i++) {
                        FilterBeacon beacon = beaconList.get(i);
                        if (beacon != null) {
                            if (isBeaconMatched(beacon, iBeacon)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    private static boolean isBeaconMatched(FilterBeacon filterBeacon, IBeacon ibeacon) {
        if (filterBeacon != null && ibeacon != null) {
            if (!filterBeacon.getUuid().equals(ibeacon.getProximityUuid())) {
                return false;
            }
            if (filterBeacon.getMajor() != 0 && filterBeacon.getMajor() != ibeacon.getMajor()) {
                return false;
            }
            if (filterBeacon.getMinor() != 0 && filterBeacon.getMinor() != ibeacon.getMinor()) {
                return false;
            }
            return true;
        }
        return false;
    }
}
