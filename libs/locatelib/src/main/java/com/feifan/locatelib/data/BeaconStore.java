package com.feifan.locatelib.data;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locatelib.cache.PlazaBeaconInfo.BeaconInfo;
import com.feifan.scanlib.beacon.RawBeacon;
import com.feifan.scanlib.beacon.SampleBeacon;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Beacon数据过滤器
 * <p>
 *     过滤非法beacon数据
 * </p>
 * Created by xuchunlei on 2016/11/9.
 */

public class BeaconStore {

    private static final BeaconStore INSTANCE = new BeaconStore();

    private Set<String> uuidFilter = new HashSet<>();
    private Set<Integer> majorFilter = new HashSet<>();
    private AvlTree<MinorExtra> minorFilter = new AvlTree<>();

    private BeaconStore() {

    }

    public static BeaconStore getInstance() {
        return INSTANCE;
    }

    public void initialize(List<BeaconInfo> data) {
        if(data != null && !data.isEmpty()) {
            uuidFilter.clear();
            majorFilter.clear();
            minorFilter.clear();

            for(BeaconInfo info : data) {
                if(!uuidFilter.contains(info.uuid)) {
                    uuidFilter.add(info.uuid);
                }
                if(!majorFilter.contains(info.major)) {
                    majorFilter.add(info.major);
                }
                minorFilter.insert(new MinorExtra(info.minor, info.floor));
            }
        }

        LogUtils.d("init plaza's uuid to " + uuidFilter.toString());
        LogUtils.d("init plaza's major to " + majorFilter.toString());
        LogUtils.d("init plaza's minor & extra to " + minorFilter.serialize());

    }

    public <T extends RawBeacon> int selectFloor(Set<T> samples) {
        Map<Integer, Integer> options = new LinkedHashMap<>();

        int rssi = -1000;    // 信号最强的beacon
        int floor2 = 0;      // 出现两次的floor
        int floor = 0;       // 最强信号的floor
        int floorCount = 0;  // 获得不同楼层数量
        for(T beacon : samples) {
            MinorExtra extra = minorFilter.find(new MinorExtra(beacon.minor, 0));
            if(extra != null) {
                LogUtils.e(extra.minor + "'s floor is " + extra.floor);
                int count = 1;
                if(options.containsKey(extra.floor)) {
                    count = options.get(extra.floor) + 1;
                    if(count >= 2) {
                        floor2 = extra.floor;
                    }
                }else {
                    floorCount++;
                }
                options.put(extra.floor, count);

                // 信号最强的beacon所在楼层
                if(rssi < beacon.rssi) {
                    floor = extra.floor;
                    rssi = beacon.rssi;
                }
            }
            else {
                LogUtils.d(beacon.minor + " is not found");
            }
        }

        LogUtils.e("floor=" + floor + ",floor2=" + floor2 + ",floorCount=" + floorCount + ",rssi=" + rssi);

        return floorCount == 3 ? floor : floor2;
    }

    public void testFloor() {
        Set<RawBeacon> data = new LinkedHashSet<>();
        RawBeacon beacon1 = new RawBeacon();
        RawBeacon beacon2 = new RawBeacon();
        RawBeacon beacon3 = new RawBeacon();

        // floor = 1; floor2 = 1; floorCount = 1;
        beacon1.uuid = "A3FCE438-627C-42B7-AB72-DC6E55E137AC";
        beacon1.rssi = -59;
        beacon1.minor = 43102;
        beacon1.mac = "1";

        beacon2.uuid = "A3FCE438-627C-42B7-AB72-DC6E55E137AC";
        beacon2.rssi = -60;
        beacon2.minor = 42663;
        beacon2.mac = "2";

        beacon3.uuid = "A3FCE438-627C-42B7-AB72-DC6E55E137AC";
        beacon3.rssi = -60;
        beacon3.minor = 42553;
        beacon3.mac = "3";

        data.add(beacon1);
        data.add(beacon2);
        data.add(beacon3);

        selectFloor(data);

        // floor = 2; floor2 = 1; floorCount = 2;
        beacon1.uuid = "A3FCE438-627C-42B7-AB72-DC6E55E137AC";
        beacon1.rssi = -59;
        beacon1.minor = 42493;
        beacon1.mac = "1";

        beacon2.uuid = "A3FCE438-627C-42B7-AB72-DC6E55E137AC";
        beacon2.rssi = -60;
        beacon2.minor = 42663;
        beacon2.mac = "2";

        beacon3.uuid = "A3FCE438-627C-42B7-AB72-DC6E55E137AC";
        beacon3.rssi = -60;
        beacon3.minor = 42553;
        beacon3.mac = "3";


        selectFloor(data);

        // floor = 2; floor2 = 0; floorCount = 3;

        // floor = 2
        beacon1.uuid = "A3FCE438-627C-42B7-AB72-DC6E55E137AC";
        beacon1.rssi = -59;
        beacon1.minor = 42493;
        beacon1.mac = "1";

        // floor = -2;
        beacon2.uuid = "A3FCE438-627C-42B7-AB72-DC6E55E137AC";
        beacon2.rssi = -60;
        beacon2.minor = 42495;
        beacon2.mac = "2";

        // floor = 1
        beacon3.uuid = "A3FCE438-627C-42B7-AB72-DC6E55E137AC";
        beacon3.rssi = -60;
        beacon3.minor = 42553;
        beacon3.mac = "3";


        selectFloor(data);

    }

    static class MinorExtra implements Comparable<MinorExtra> {
        int minor;
        int floor;

        public MinorExtra(int minor, int floor) {
            this.minor = minor;
            this.floor = floor;
        }

        @Override
        public int compareTo(MinorExtra o) {
            return minor - o.minor;
        }

        @Override
        public String toString() {
            return minor + "(" + floor + ")";
        }
    }
}
