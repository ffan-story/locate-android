package com.feifan.locatelib.algorithm;

import com.feifan.locatelib.cache.FingerprintStore.FPLocation;

/**
 * 位置检验器
 * <p>
 *     滤波器，将不合理的计算结果过滤掉
 * </p>
 * Created by xuchunlei on 2016/12/1.
 */

public interface ILocationInspector {

    /**
     * 检查位置是否合法
     * @param loc 最佳位置
     * @param minR 最佳结果半径
     * @return
     */
    FPLocation inspect(FPLocation loc, double minR);

    /**
     * 更新阈值参数
     * @param values
     */
    void updateThreshold(double[] values);
}
