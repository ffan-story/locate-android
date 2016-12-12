package com.feifan.sensorlib.process;

/**
 * 方向监听接口
 * Created by xuchunlei on 2016/12/8.
 */

public interface OrientationListener {
    /**
     * 方向变化
     * @param radian
     */
    void onOrientationChanged(float radian);
}
