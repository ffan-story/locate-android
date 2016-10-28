package com.feifan.locate;

import android.app.Application;
import android.content.Context;

import com.rtm.frm.map.XunluMap;

/**
 * Created by xuchunlei on 2016/10/27.
 */

public class LocateApplication extends Application {
    public static Context CONTEXT;

    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = getApplicationContext();

        XunluMap.getInstance().setContext(this);
        XunluMap.getInstance().setUrlRelease("http://map.wifi.ffan.com/");
    }
}
