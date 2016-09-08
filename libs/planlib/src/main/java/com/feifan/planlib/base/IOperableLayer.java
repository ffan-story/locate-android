package com.feifan.planlib.base;

/**
 * 可操作的图层，继承自{@link ILayer}
 * <p>
 *     实现此接口的图层，可以在平面图中添加、移除和查找坐标点
 * </p>
 * Created by xuchunlei on 16/9/7.
 */
public interface IOperableLayer extends ILayer {
    /**
     * 添加坐标点
     * @param x  显示位置横坐标
     * @param y  显示位置纵坐标
     * @param rx 实际位置横坐标
     * @param ry 实际位置纵坐标
     * @return 平面图坐标点
     */
    ILayerPoint add(float x, float y, float rx, float ry);

    /**
     * 移除坐标点
     * @param point 坐标点
     * @return 成功，返回移除的坐标点，否则返回null
     */
    boolean remove(ILayerPoint point);

    /**
     * 通过触碰坐标寻找坐标点
     *
     * @param x
     * @param y
     * @return
     */
    ILayerPoint findPointByTouch(float x, float y);
}
