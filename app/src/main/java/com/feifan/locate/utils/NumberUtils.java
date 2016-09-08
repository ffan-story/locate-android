package com.feifan.locate.utils;

import com.feifan.baselib.utils.LogUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static float degree(float radian) {
        float degree =  (float) Math.toDegrees(radian);
        if(degree < 0) {
            degree += 360f;
        }
        return degree;
    }

    /**
     * 判断字符串是否为数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
}
