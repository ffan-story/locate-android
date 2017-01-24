package com.feifan.locatelib.utils;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.scanlib.beacon.RawBeacon;

import java.util.Collection;

/**
 * Created by xuchunlei on 2016/11/24.
 */

public class PrintUtils {

    private PrintUtils() {

    }

    /**
     * 打印集合
     * @param data
     */
    public static<T> void printCollection(Collection<T> data) {
        for(T item : data) {
            LogUtils.d(item.toString());
        }
    }

    /**
     * 打印数组
     * @param data
     * @param <T>
     */
    public static <T> void printArray(T[] data) {
        for(T item : data) {
            LogUtils.d(item.toString());
        }
    }
}
