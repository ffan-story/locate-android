package com.feifan.locatelib.online;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xuchunlei on 16/9/14.
 */
public class LocateResultBean {

    @SerializedName("error_code")
    public int errorCode;

    public LocateInfo info;

    public static class LocateInfo {

        @SerializedName("loc_x")
        public float x;

        @SerializedName("loc_y")
        public float y;
    }
}
