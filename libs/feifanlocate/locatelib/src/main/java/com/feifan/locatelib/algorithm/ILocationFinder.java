package com.feifan.locatelib.algorithm;

import android.content.Context;

import com.feifan.locatelib.cache.FingerprintStore.FPLocation;
import com.feifan.scanlib.beacon.SampleBeacon;

import java.util.List;
import java.util.Map;

/**
 * Created by xuchunlei on 2016/12/1.
 */

public interface ILocationFinder {

    /**
     * 初始化
     * @param inspector
     */
    void initialize(Map<String, Integer> beaconMap,
                    ILocationInspector inspector);

    /**
     * 开启补偿
     * @param context
     */
    void startCompensate(Context context);

    /**
     * 停止补偿
     * @param context
     */
    void stopCompensate(Context context);
    /**
     * 更新使用的指纹库
     * @param fps
     */
    void updateFingerprints(FPLocation[] fps);
    /**
     * 选取位置
     * @param samples
     * @return
     */
    FPLocation selectLocation(List<SampleBeacon> samples);
}
