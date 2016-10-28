package com.feifan.maplib.entity;

/**
 * Created by xuchunlei on 2016/10/27.
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
     * 清除端点
     * @param point
     */
    void clearPoint(ILayerPoint point);
}
