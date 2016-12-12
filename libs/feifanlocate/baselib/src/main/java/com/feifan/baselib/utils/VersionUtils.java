package com.feifan.baselib.utils;

import android.os.Build;

/**
 * 版本工具类
 * Created by xuchunlei on 16/9/1.
 */
public class VersionUtils {

    /**
     * 设备运行的Android版本号高于（包含）21
     */
    public static final boolean LARGER_THAN_21 = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);

    private VersionUtils(){

    }
}
