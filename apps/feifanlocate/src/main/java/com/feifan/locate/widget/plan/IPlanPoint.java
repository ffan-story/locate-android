package com.feifan.locate.widget.plan;

/**
 * 平面图坐标点
 * <pre>
 *     包含用于绘制的坐标点和用于计算位置的坐标点信息，绘制坐标点使用承载平面图的View坐标系
 *     位置坐标系使用平面图坐标系
 * </pre>
 *
 * Created by xuchunlei on 16/8/26.
 */
public interface IPlanPoint {

    /**
     * 获取实际位置横坐标
     * @return
     */
    float getRawX();

    /**
     * 获取实际位置纵坐标
     * @return
     */
    float getRawY();
    /**
     * 平移绘制坐标
     * @param deltaX
     * @param deltaY
     */
    void offset(float deltaX, float deltaY);

    /**
     * 平移计算坐标
     * @param deltaX
     * @param deltaY
     */
    void offsetRaw(float deltaX, float deltaY);

    /**
     * 设置绘制坐标
     * @param x
     * @param y
     */
    void set(float x, float y);

    /**
     * 设置计算坐标
     * @param x
     * @param y
     */
    void setRaw(float x, float y);

}
