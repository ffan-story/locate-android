package com.mm.stock.util;

import android.util.Log;

import com.mm.stock.BuildConfig;


/**
 * Created by xuchunlei on 15/6/24.
 */
public class LogUtil {

    private LogUtil() {

    }

    public static void d(String tag, String message) {
        if(BuildConfig.DEBUG && null != message) {
            Log.d(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if(BuildConfig.DEBUG && null != message) {
            Log.i(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if(BuildConfig.DEBUG && null != message) {
            Log.w(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if(BuildConfig.DEBUG && null != message) {
            Log.e(tag, message);
        }
    }
}
