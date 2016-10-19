package com.feifan.planlib;

/**
 * 平面图线
 * <p>
 *     包含两个端点
 * </p>
 *
 * Created by bianying on 2016/10/16.
 */
public interface ILayerLine {

    /**
     * 设置ID
     * @param id
     */
    void setId(int id);

    /**
     * 获取ID
     * @return
     */
    int getId();

    /**
     * 获取端点1
     * @return
     */
    ILayerPoint getPointOne();

    /**
     * 获取端点2
     * @return
     */
    ILayerPoint getPointTwo();

    /**
     * 设置端点1
     * @param point
     */
    void setPointOne(ILayerPoint point);

    /**
     * 设置端点2
     * @param point
     */
    void setPointTwo(ILayerPoint point);

    /**
     * 清除端点
     * @param point
     */
    void clearPoint(ILayerPoint point);

}
