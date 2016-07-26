package com.wanda.logger.log;

/**
 * Created by mengmeng on 15/6/8.
 */
public class Settings {
    private String TAG = "";
    private boolean isShowMethord = true;


    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }


    public boolean isShowMethord() {
        return isShowMethord;
    }

    public void setIsShowMethord(boolean isShowMethord) {
        this.isShowMethord = isShowMethord;
    }

}
