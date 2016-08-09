package com.feifan.locate.utils;

import android.content.Context;

/**
 * 尺寸相关的工具类
 *
 * Created by xuchunlei on 16/8/9.
 */
public class SizeUtils {

    private SizeUtils() {

    }

    /**
     * dp转px
     * @param context
     * @param dpValue
     * @return
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }
}
