package com.feifan.locatelib.cache;

import android.util.Log;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locatelib.cache.model.PlazaBeaconInfo.BeaconInfo;
import com.feifan.locatelib.data.AvlTree;
import com.feifan.locatelib.utils.PrintUtils;
import com.feifan.scanlib.beacon.RawBeacon;
import com.feifan.scanlib.beacon.SampleBeacon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
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

    // filter & sort
    private final Map<String, Float> resultMapCache = new LinkedHashMap<>();
    private final List<SampleBeacon> resultListCache = new LinkedList<>();
    private final Map<String, Integer> countCache = new HashMap<>();
    private static final int TOP_SIZE = 3;
    private final List<RawBeacon> beaconPool = new ArrayList<>();


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
//            minorMap.clear();
//            sortCache.clear();

            for(BeaconInfo info : data) {
                if(!uuidFilter.contains(info.uuid)) {
                    uuidFilter.add(info.uuid.toLowerCase());
                }
                if(!majorFilter.contains(info.major)) {
                    majorFilter.add(info.major);
                }
                minorFilter.insert(new MinorExtra(info.minor, info.floor));
//                sortCache.add(info.major + "_" + info.minor);
            }

            LogUtils.d("there are " + data.size() + " beacons in plaza");
        }

        // 初始化minorMap
//        Collections.sort(sortCache);
//        for(int i = 0;i < sortCache.size();i++) {
//            String key = sortCache.get(i);
////            int minorIndex = key.indexOf("-");
////            int minor = Integer.valueOf(key.substring(minorIndex + 1, key.length()));
//            minorMap.put(key, i);
//        }

//        beaconCount = data == null ? 0 : data.size();
//        LogUtils.d("there are " + beaconCount + " beacons in plaza");
        LogUtils.d("init plaza's uuid to " + uuidFilter.toString());
        LogUtils.d("init plaza's major to " + majorFilter.toString());
        LogUtils.d("init plaza's minor & extra to " + minorFilter.serialize());

//        // temp
//        loadTest();
//        beaconCount = 38;

    }

    private Comparator<RawBeacon> comparator = new Comparator<RawBeacon>() {
        @Override
        public int compare(RawBeacon r1, RawBeacon r2) {
            return r2.rssi - r1.rssi;
        }
    };

    public synchronized List<SampleBeacon> process2List(Collection<SampleBeacon> data) {
        doProcess(data);
        float value;

        Set<Map.Entry<String, Integer>> cacheSet = countCache.entrySet();

        int i = 0;
        for (Map.Entry<String, Integer> entry : cacheSet) {
            value = resultMapCache.get(entry.getKey());
            resultListCache.get(i++).rssi = (int)value / entry.getValue();
        }
        return resultListCache;
    }

    public synchronized Map<String, Float> process2Map(Collection<SampleBeacon> data) {
        doProcess(data);
        float value;
        Set<Map.Entry<String, Integer>> cacheSet = countCache.entrySet();
        for (Map.Entry<String, Integer> entry : cacheSet) {
            value = resultMapCache.get(entry.getKey());
            resultMapCache.put(entry.getKey(), value / entry.getValue());
        }
        return resultMapCache;
    }


    private void doProcess(Collection<SampleBeacon> rawData) {
        resultListCache.clear();
        resultMapCache.clear();
        countCache.clear();

        if(rawData != null && rawData.size() != 0) {
            String key;
            Float value;

            MinorExtra extra = new MinorExtra(0, 0);
            for(SampleBeacon sample : rawData) {
                extra.minor = sample.minor;
                if(!uuidFilter.contains(sample.uuid)) {
                    continue;
                } else if(!majorFilter.contains(sample.major)) {
                    continue;
                } else if(!minorFilter.contains(extra)) {
                    continue;
                }
                key = sample.uuid + "_" + sample.major + "_" + sample.minor;
                value = resultMapCache.get(key);
                if(value != null) {
                    resultMapCache.put(key, value + sample.rssi);
                    countCache.put(key, countCache.get(key) + 1);
                }else {
                    resultMapCache.put(key, (float)sample.rssi);
                    countCache.put(key, 1);
                }
                resultListCache.add(sample);
            }
        }
    }

    /*--------------确定楼层---------------*/
    private Map<Integer, Integer> options = new LinkedHashMap<>();
    private Set<RawBeacon> topBeaconSet = new LinkedHashSet<>();

    /**
     * 定位楼层
     * @param samples
     * @return 0 表示未能成功定位楼层，其他表示楼层编号，1表示1层，-1表示-1层，以此类推
     */
    public<T extends RawBeacon> int selectFloor(Collection<T> samples) {
        trimTopByRssi(samples, TOP_SIZE);
        if(TOP_SIZE == 3) {
            return computeFloorFromTop3(topBeaconSet);
        }

        if(TOP_SIZE == 5) {
            return computeFloorFromTop5(topBeaconSet);
        }

        return 0;
    }

    // 裁剪集合中信号最强的三个Beacon
    private<T extends RawBeacon> void trimTopByRssi(Collection<T> data, int k) {
        beaconPool.clear();
        beaconPool.addAll(data);
        Collections.sort(beaconPool);
        topBeaconSet.clear();

        for(RawBeacon beacon : beaconPool) {
            if(!topBeaconSet.contains(beacon)) {
                if(!uuidFilter.contains(beacon.uuid)) {
                    continue;
                }else if(!majorFilter.contains(beacon.major)) {
                    continue;
                }
                topBeaconSet.add(beacon);

            }
            if(topBeaconSet.size() == k) {
                break;
            }
        }
    }

    // 使用信号最强的3个beacon计算楼层
    private int computeFloorFromTop3(Set<RawBeacon> data) {
        options.clear();

        int rssi = -1000;    // 信号最强的beacon
        int floor2 = 0;      // 出现两次的floor
        int floor = 0;       // 最强信号的floor
        int floorCount = 0;  // 获得不同楼层数量

        for(RawBeacon beacon : data) {
            LogUtils.d("top rssi beacon:" + beacon.major + "," + beacon.minor);
            MinorExtra extra = minorFilter.find(new MinorExtra(beacon.minor, 0));
            if(extra != null) {
                int count = 1;
                if(options.containsKey(extra.floor)) {
                    count = options.get(extra.floor) + 1;
                    if(count >= 2) { // 过半数的楼层
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

        LogUtils.d("floor=" + floor + ",floor2=" + floor2 + ",floorCount=" + floorCount + ",rssi=" + rssi);

        if(data.size() < 3) { // 样本数不足
            LogUtils.w("valid samples' count is " + data.size());
            return floor;
        }

        return floorCount == 3 ? floor : floor2;
    }

    // 使用信号最强的5个beacon计算楼层
    private int computeFloorFromTop5(Set<RawBeacon> data) {

        int rssi = -1000;    // 最强信号强度，用于鉴别信号最强的beacon
        int rssi2 = -1000;   // 出现两次的floor使用的beacon最强信号强度
        int floor3 = 0;      // 出现三次的floor
        int floor2_1 = 0;    // 出现两次的floor
        int floor2_2 = 0;    // 出现两次的floor
        int floor = 0;       // 最强信号的floor

        final int dataCount = data.size();
        RawBeacon beacon = null;
        for(int i = dataCount - 1;i >= 0;i--) { // 倒序遍历，方便更新最强beacon

            MinorExtra extra = minorFilter.find(new MinorExtra(beacon.minor, 0));
            if(extra != null) {
                int count = 1;
                if(options.containsKey(extra.floor)) {
                    count = options.get(extra.floor) + 1;
                    if(count == 2) { // 出现过两次的楼层
                        if(floor2_1 == 0) { // 第一个出现两次的楼层
                            floor2_1 = extra.floor;
                        }else {     // 找到第二个出现2次的楼层，此时楼层分布为1，2，2
                            floor2_2 = extra.floor;
                        }
                    }
                    if(count >= 3) { // 出现超过三次的楼层，分布可能为3，1，1；3，2；4，1；5
                        floor3 = extra.floor;
                    }
                }
                options.put(extra.floor, count);

                // 信号最强的beacon所在楼层
                if(rssi < beacon.rssi) {
                    floor = extra.floor;
                    rssi = beacon.rssi;
                }
            }
        }



        if(floor3 !=0) {
            return floor3;
        }


        return 0;
    }

    // 裁剪集合中信号最强的三个Beacon
    /*
    private void trimTopByRssi(RawBeacon[] data, int k) {
        Arrays.sort(data, comparator);
        topBeaconSet.clear();

        for(RawBeacon beacon : data) {
            if(!topBeaconSet.contains(beacon)) {
                if(!uuidFilter.contains(beacon.uuid)) {
                    continue;
                }else if(!majorFilter.contains(beacon.major)) {
                    continue;
                }
                topBeaconSet.add(beacon);
            }
            if(topBeaconSet.size() == k) {
                break;
            }
        }
    }
*/
    /**
     * 定位楼层
     * @param samples
     * @return
     */
    /*
    public int selectFloor(RawBeacon[] samples) {
        trimTopByRssi(samples, 3);
//        for(RawBeacon b : topBeaconSet) {
//            LogUtils.e(b.uuid + ", major=" + b.major + ",minor=" + b.minor);
//        }
        options.clear();

        int rssi = -1000;    // 信号最强的beacon
        int floor2 = 0;      // 出现两次的floor
        int floor = 0;       // 最强信号的floor
        int floorCount = 0;  // 获得不同楼层数量

        for(RawBeacon beacon : topBeaconSet) {
            LogUtils.d("top rssi beacon:" + beacon.major + "," + beacon.minor);
            MinorExtra extra = minorFilter.find(new MinorExtra(beacon.minor, 0));
            if(extra != null) {
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

        LogUtils.d("floor=" + floor + ",floor2=" + floor2 + ",floorCount=" + floorCount + ",rssi=" + rssi);

        int total = samples.length > 3 ? 3 : samples.length;
        if(total < 3) { // 样本数不足3个
            LogUtils.w("valid samples' count is " + total);
            return floor;
        }

        return floorCount == 3 ? floor : floor2;
    }
*/

    /*--------------确定楼层 end---------------*/

//    public Map<String, Integer> getMinorMap() {
//        return minorMap;
//    }

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

    private void loadTest() {
        // B31

        int[] minor_860100010030500015 = new int[] {
                101,102,103,104,105,108,109,110,111,112,
                113,114,115,116,117,118,119,120,125,126,
                127,128,129,130,131,132,133,134,135,136,
                137,140,141,142,143,144,145,146
        };

        uuidFilter.add("ecb33b47-781f-4c16-8513-73fcbb7134f2");
        majorFilter.add(100);
        for(int minor : minor_860100010030500015) {
            minorFilter.insert(new MinorExtra(minor, 31));
        }
//        FILTER_MAP_UUID.put(KEY_860100010030500015, "ecb33b47-781f-4c16-8513-73fcbb7134f2");
//        FILTER_MAP_MAJOR.put(KEY_860100010030500015, "100");

    }
    /*
    public void testFloor() {
//        Set<RawBeacon> data = new LinkedHashSet<>();
        RawBeacon beacon1 = new RawBeacon();
        RawBeacon beacon2 = new RawBeacon();
        RawBeacon beacon3 = new RawBeacon();
        RawBeacon[] data = new RawBeacon[]{
                beacon1,
                beacon2,
                beacon3
        };

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
    */
}
