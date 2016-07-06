package com.feifan.sampling.base.log;

import android.content.Context;

import com.feifan.sampling.SampleApplication;

/**
 * Created by mengmeng on 16/7/6.
 */
public class GlobalState {
    private static Context mContext;

    public static void init(Context context){
        mContext = context;
    }
    public static boolean isOnLineState(){
        SampleApplication app = (SampleApplication) mContext.getApplicationContext();
        return app.isServiceOn();
    }
}
