package com.feifan.locatelib.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xuchunlei on 16/9/19.
 */
public class HttpResult<T> {
//    @SerializedName("error_code")
//    public int errorCode;
//    public T info;

    public int status;
    public String message;

    public T data;
}
