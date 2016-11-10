package com.feifan.indoorlocation.model;

import java.io.Serializable;

/**
 * Created by yangrenyong on 2016/11/2.
 */

public class Beacon implements Serializable {
    public final String name; // beacon 名字，必须
    public final String proximityUuid; // beacon proximity UUID， 必须
    public final int major; // beacon major， 必须
    public final int minor; // beacon minor， 必须
    public final String bluetoothAddress; // beacon 蓝牙MAC地址， 可选

    public Beacon(String name, String proximityUuid, int major, int minor,
                  String bluetoothAddress) {
        this.name = name;
        this.proximityUuid = proximityUuid;
        this.major = major;
        this.minor = minor;
        this.bluetoothAddress = bluetoothAddress;
    }
}

