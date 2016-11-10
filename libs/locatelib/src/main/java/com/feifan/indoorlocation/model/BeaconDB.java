package com.feifan.indoorlocation.model;

import java.util.List;

/**
 * Created by xuchunlei on 2016/11/9.
 */

public class BeaconDB {
    public final IndoorLocationInfoModel plaza;
    public final String token;
    public final List<BeaconLocation> beaconLocations;

    public BeaconDB(IndoorLocationInfoModel plaza, List<BeaconLocation> locations) {
        this.plaza = plaza;
        token = "";
        this.beaconLocations = locations;
    }
}
