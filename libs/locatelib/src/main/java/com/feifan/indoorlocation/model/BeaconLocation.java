package com.feifan.indoorlocation.model;

/**
 * Created by xuchunlei on 2016/11/9.
 */

public class BeaconLocation {
    public final Beacon beacon;
    public final double x;
    public final double y;
    public final int floor;

    public BeaconLocation(Beacon beacon) {
        this.beacon = beacon;
        x = 0d;
        y = 0d;
        floor = 0;
    }
}
