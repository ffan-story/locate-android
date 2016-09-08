// IScanService.aidl
package com.feifan.scanlib;

// Declare any non-default types here with import statements

interface IScanService {

    /**
     * 启动扫描
     * @param x 扫描点位置横坐标
     * @param y 扫描点位置纵坐标
     * @param initRadian 初始方位
     */
    void startScan(float x, float y, float initRadian);

    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
