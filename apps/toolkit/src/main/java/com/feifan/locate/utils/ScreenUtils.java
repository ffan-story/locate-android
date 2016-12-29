package com.feifan.locate.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.LocateApplication;

/**
 * Created by xuchunlei on 16/9/21.
 */
public class ScreenUtils {

    private ScreenUtils() {

    }

    private static int SCREEN_WIDTH = 0;
    private static int SCREEN_HEIGHT = 0;
    /**
     * 获取屏幕的宽度px
     *
     * @return 屏幕宽px
     */
    public static int getScreenWidth() {
        if(SCREEN_WIDTH == 0) {
            WindowManager windowManager = (WindowManager) LocateApplication.CONTEXT.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();// 创建了一张白纸
            windowManager.getDefaultDisplay().getMetrics(outMetrics);// 给白纸设置宽高
            SCREEN_WIDTH = outMetrics.widthPixels;
            LogUtils.d("screen's width=" + SCREEN_WIDTH);
        }
        return SCREEN_WIDTH;
    }

    /**
     * 获取屏幕的高度px
     *
     * @return 屏幕高px
     */
    public static int getScreenHeight() {
        if(SCREEN_HEIGHT == 0) {
            WindowManager windowManager = (WindowManager) LocateApplication.CONTEXT.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();// 创建了一张白纸
            windowManager.getDefaultDisplay().getMetrics(outMetrics);// 给白纸设置宽高
            SCREEN_HEIGHT = outMetrics.heightPixels;
            LogUtils.d("screen's height=" + SCREEN_HEIGHT);
        }

        return SCREEN_HEIGHT;
    }
}
