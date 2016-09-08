package com.feifan.locate.utils;

/**
 * Created by xuchunlei on 16/8/30.
 */
public class NumberUtils {

    private NumberUtils() {

    }

    public static float round(float value, int precision) {
        int precisionValue = (int)Math.pow(10, precision);
        return (float)(Math.round(value * precisionValue)) / precisionValue;
    }
}
