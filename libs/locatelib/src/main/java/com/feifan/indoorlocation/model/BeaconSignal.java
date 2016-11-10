package com.feifan.indoorlocation.model;

/**
 * Created by xuchunlei on 2016/11/9.
 */

public class BeaconSignal {
    public final Beacon beacon;
    public final int txPower;
    public final int rssi;

    public BeaconSignal(Beacon beacon) {
        this.beacon = beacon;
        txPower = 0;
        rssi = 0;
    }
}
