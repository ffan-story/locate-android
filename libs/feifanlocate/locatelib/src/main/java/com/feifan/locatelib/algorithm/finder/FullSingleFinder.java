package com.feifan.locatelib.algorithm.finder;

import com.feifan.locatelib.cache.FingerprintStore.FPLocation;
import com.feifan.locatelib.cache.FingerprintStore.FPFeature;
import com.feifan.scanlib.beacon.SampleBeacon;

import java.util.List;

/**
 * Created by xuchunlei on 2016/12/1.
 */

public class FullSingleFinder extends FinderBase {

    private static FullSingleFinder INSTANCE = new FullSingleFinder();

    private FullSingleFinder() {

    }

    public static FullSingleFinder getInstance() {
        return INSTANCE;
    }

    @Override
    public FPLocation selectLocation(List<SampleBeacon> samples) {
        // 构建样本特征向量
        System.arraycopy(featureSrc, 0, featureCache, 0, featureCount);
        for(SampleBeacon beacon : samples) {
            featureCache[mBeaconMap.get(beacon.toIdentityString())] = (byte) beacon.rssi;
        }

        FPLocation minLoc = null;
        int min = Integer.MAX_VALUE;
        for(FPLocation loc : mFPLocations) {
            final FPFeature[] feasures = loc.features;
            int i;
            int currentPos = 0;
            int sum = 0;
            int sub;
            for(FPFeature feasure : feasures) {
                for(i = currentPos;i < feasure.index - 1;i++) {
                    // 计算非法特征值差平方之和
                    sub = featureCache[i] + 99;
                    sum += sub * sub;
                }
                // 计算合法特征项的差平方之和
                sub = featureCache[feasure.index] - feasure.rssi;
                sum += sub * sub;
                currentPos = feasure.index + 1;
            }

            for(i = currentPos;i < featureCount;i++) {
                sub = featureCache[i] + 99;
                sum += sub * sub;
            }
            if(sum < min) {
                min = sum;
                minLoc = loc;
            }
        }

        return minLoc;
    }
}
