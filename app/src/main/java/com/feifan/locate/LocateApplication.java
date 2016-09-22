package com.feifan.locate;

import android.app.Application;
import android.content.Context;

/**
 * Created by xuchunlei on 16/9/21.
 */
public class LocateApplication extends Application {

    public static Context CONTEXT;

    @Override
    public void onCreate() {
        super.onCreate();

        CONTEXT = getApplicationContext();
    }
}
