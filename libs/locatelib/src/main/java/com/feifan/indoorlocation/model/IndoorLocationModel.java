package com.feifan.indoorlocation.model;

import java.io.Serializable;

/**
 * Created by yangrenyong on 2016/11/2.
 */

public class IndoorLocationModel implements Serializable {
    public double x; // 定位的x坐标，必须
    public double y; // 定位的y坐标，必须
    public int floor; // 比如地下一层为-1, 地上一层为1, 地上二层为2
    public IndoorLocationInfoModel locationInfo; // 定位点位置信息，必须
    public long timestamp; // 时间戳，必须

    public IndoorLocationModel() {
        locationInfo = new IndoorLocationInfoModel();
    }

    public IndoorLocationModel(double x, double y, int floor, IndoorLocationInfoModel locationInfo,
                               long timestamp) {
        this.x = x;
        this.y = y;
        this.floor = floor;
        this.locationInfo = locationInfo;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return x + "," + y + "," + floor + " at " + timestamp;
    }
}

