package com.feifan.locate.sampling;

import com.feifan.scanlib.beacon.SampleBeacon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据修复器
 * <p>
 *     修复非beacon数据的uuid、major和minor
 * </p>
 * Created by xuchunlei on 16/9/29.
 */

public final class DataFixer {

    private static final Map<String, Map<String, FixItem>> FIXER_CACHE;
    private static final String KEY_A22 = "A22";
    private static final String KEY_B31 = "B31";
    private static final String KEY_860100010060300001 = "860100010060300001";
    static {
        FIXER_CACHE = new HashMap<>();

        // A22
        Map<String, FixItem> fixMap_A22 = new HashMap<>();
        FIXER_CACHE.put(KEY_A22, fixMap_A22);

        // A31
        Map<String, FixItem> fixMap_B31 = new HashMap<>();
        FIXER_CACHE.put(KEY_B31, fixMap_B31);

        // 860100010060300001
        Map<String, FixItem> fixMap_860100010060300001 = new HashMap<>();
        FIXER_CACHE.put(KEY_860100010060300001, fixMap_860100010060300001);
    }

    private DataFixer() {

    }

    public static void FixBeacons(String where, List<SampleBeacon> beacons) {
        Map<String, FixItem> matcher = FIXER_CACHE.get(where);
        if(matcher == null) {
            throw new UnsupportedOperationException("we do not support fixing in building " + where);
        }else {
            for(SampleBeacon beacon : beacons) {
                FixItem fixItem = matcher.get(beacon.mac);
                if(fixItem != null) {
                    beacon.uuid = fixItem.uuid;
                    beacon.major = fixItem.major;
                    beacon.minor = fixItem.minor;
                }
            }
        }
    }

    private static class FixItem {
        public String uuid;
        public int major;
        public int minor;

        public FixItem(String uuid, int major, int minor) {
            this.uuid = uuid;
            this.major = major;
            this.minor = minor;
        }
    }
}
