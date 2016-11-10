package com.feifan.indoorlocation;

/**
 * Created by yangrenyong on 2016/11/2.
 */

public class IndoorLocationError {

    public final int errorCode; // 错误码，必须
    public final String errorMsg; // 错误信息，必须

    public IndoorLocationError(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
