package com.feifan.scanlib;

import com.feifan.scanlib.beacon.RawBeacon;

/**
 * 区域
 * <p>
 *     表示采集特定区域的beacon数据，一般使用beacon的uuid和major定义区域
 * </p>
 * Created by xuchunlei on 2016/11/23.
 */

public class Region {
    private String uuid = "";
    private int major = -1;

    public Region(String uuid) {
        if(uuid != null) {
            this.uuid = uuid;
        }
    }
    public Region(String uuid, int major) {
        this(uuid);
        this.major = major;
    }

    public<T extends RawBeacon> boolean contains(T beacon) {
        return uuid.equalsIgnoreCase(beacon.uuid) && (major == -1 || major == beacon.major);
    }
}
