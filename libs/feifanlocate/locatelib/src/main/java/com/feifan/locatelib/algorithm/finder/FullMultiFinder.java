package com.feifan.locatelib.algorithm.finder;

import com.feifan.debuglib.window.DebugWindow;
import com.feifan.locatelib.cache.FingerprintStore.FPLocation;
import com.feifan.locatelib.cache.FingerprintStore.FPFeature;
import com.feifan.scanlib.beacon.SampleBeacon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 多点质心发现器
 * Created by xuchunlei on 2016/12/1.
 */

public class FullMultiFinder extends FinderBase {
    private static FullMultiFinder INSTANCE = new FullMultiFinder();

    // 选择系数,欧拉距离在min * SELECTION_COEFFICIENT作为结果集合
    private static final float SELECTION_COEFFICIENT = 1.2f;

    // 结果集
    private TreeMap<Integer, FPLocation> resultCache = new TreeMap<>(); // 指纹计算结果和位置
    private Map<FPLocation, Double> resultWeight = new HashMap<>();      // 保存最优解及其权重集合
    // 最优解
    private FPLocation optimum = new FPLocation();

    private FullMultiFinder() {

    }

    public static FullMultiFinder getInstance() {
        return INSTANCE;
    }

    @Override
    public FPLocation selectLocation(List<SampleBeacon> samples) {
        reset();

        for(SampleBeacon beacon : samples) {
            featureCache[mBeaconMap.get(beacon.toIdentityString())] = (byte) beacon.rssi;
        }

//        FPLocation minLoc = null;
        for(FPLocation loc : mFPLocations) {
            final FPFeature[] feasures = loc.features;
            int k;
            int currentPos = 0;
            int sum = 0;
            int sub;
            for (FPFeature feasure : feasures) {
                for (k = currentPos; k < feasure.index - 1; k++) {
                    // 计算非法特征值差平方之和
                    sub = featureCache[k] + 99;
                    sum += sub * sub;
                }
                // 计算合法特征项的差平方之和
                sub = featureCache[feasure.index] - feasure.rssi;
                sum += sub * sub;
                currentPos = feasure.index + 1;
            }

            for (k = currentPos; k < featureCount; k++) {
                sub = featureCache[k] + 99;
                sum += sub * sub;
            }
            resultCache.put(sum, loc);
        }

        if(resultCache.isEmpty()) {
            return null;
        }

        int floor = resultCache.firstEntry().getValue().floor;

        // 计算权重
        double minDis = Math.sqrt(resultCache.firstKey());
        double selectRadius = minDis * SELECTION_COEFFICIENT;
        double weightSum = 0;
        Map.Entry<Integer, FPLocation> entry;
        while ((entry = resultCache.pollFirstEntry()) != null) {
            double dis = Math.sqrt(entry.getKey());
            if(dis >= selectRadius) {
                break;
            }
            double weight = SELECTION_COEFFICIENT - (dis / minDis) * 0.2;
            weightSum += weight;
            resultWeight.put(entry.getValue(), weight);
        }

        // 计算加权平均值
        double x = 0;
        double y = 0;
        for(Map.Entry<FPLocation, Double> e : resultWeight.entrySet()) {
            x +=  e.getKey().x * (e.getValue() / weightSum);
            y +=  e.getKey().y * (e.getValue() / weightSum);
        }

        optimum.x = (float) x;
        optimum.y = (float) y;
        optimum.floor = floor;

        FPLocation loc = mInspector.inspect(optimum, minDis);
        if(loc == null) { // 使用PDR预测位置
            if(!mPredictor.isInited()) {
                mPredictor.setReference(optimum);
            }
            loc = mPredictor.updatePredictedLocation();
//            DebugWindow.get().logE(System.currentTimeMillis() / 1000 + ":" + loc.x + "," + loc.y + "," + loc.floor);
        } else {          // 使用指纹位置
            mPredictor.setReference(loc); // 设置有效位置
//            DebugWindow.get().logI(System.currentTimeMillis() / 1000 + ":" + loc.x + "," + loc.y + "," + loc.floor);
        }

        return loc;
    }

    private void reset() {
        System.arraycopy(featureSrc, 0, featureCache, 0, featureCount);
        resultCache.clear();
        resultWeight.clear();
    }
}
