package com.feifan.locatelib.cache;

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

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof LocateInfo)) {
            return false;
        }
        LocateInfo info = (LocateInfo)obj;
        return (x == info.x) && (y == info.y) && (floor == info.floor);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + Float.floatToIntBits(x);
        result = 31 * result + Float.floatToIntBits(y);
        result = 31 * result + floor;
        return result;
    }
}
