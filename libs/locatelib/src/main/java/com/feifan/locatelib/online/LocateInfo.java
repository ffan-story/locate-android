package com.feifan.locatelib.online;

import com.google.gson.annotations.SerializedName;

/**
 * 在线定位结果信息
 * Created by xuchunlei on 16/9/14.
 */
public class LocateInfo {

    @SerializedName("loc_x")
    public float x;

    @SerializedName("loc_y")
    public float y;

    public int floor;
}
