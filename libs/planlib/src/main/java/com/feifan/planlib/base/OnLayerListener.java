package com.feifan.planlib.base;

/**
 * 平面图层监听接口
 * <p>
 *     用于图层与平面图之间的交互，{@link com.feifan.planlib.PlanView}实现该接口
 * </p>
 *
 * Created by xuchunlei on 16/9/7.
 */
public interface OnLayerListener {
    /**
     * 图层数据变更通知
     */
    void notifyLayerDataChanged();
}
