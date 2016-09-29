package com.feifan.planlib;

import com.feifan.baselib.utils.LogUtils;

/**
 * Created by xuchunlei on 16/9/27.
 */

public class LayerPoint implements ILayerPoint {

    private int id;

    // 绘制坐标
    private float locX;
    private float locY;

    // 平面图坐标
    private float rawX;
    private float rawY;

    // 实际坐标
    private float realX;
    private float realY;

    // 平面图比例尺
    private float scale;

    public LayerPoint() {
        realX = -10;
        realY = -10;
        scale = 1;
    }

    /**
     * 设置索引编号
     *
     * @param id
     */
    @Override
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取索引编号
     *
     * @return
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * 获取实际位置横坐标
     *
     * @return
     */
    @Override
    public float getRawX() {
        return rawX;
    }

    /**
     * 获取实际位置纵坐标
     *
     * @return
     */
    @Override
    public float getRawY() {
        return rawY;
    }

    /**
     * 获取绘制位置横坐标
     *
     * @return
     */
    @Override
    public float getLocX() {
        return locX;
    }

    /**
     * 获取绘制位置纵坐标
     *
     * @return
     */
    @Override
    public float getLocY() {
        return locY;
    }

    /**
     * 获取真实位置横坐标
     *
     * @return
     */
    @Override
    public float getRealX() {
        return realX;
    }

    /**
     * 获取真实位置纵坐标
     *
     * @return
     */
    @Override
    public float getRealY() {
        return realY;
    }

    /**
     * 平移绘制坐标
     *
     * @param deltaX
     * @param deltaY
     */
    @Override
    public void offset(float deltaX, float deltaY) {
        locX += deltaX;
        locY += deltaY;
    }

    /**
     * 平移计算坐标
     *
     * @param deltaX
     * @param deltaY
     */
    @Override
    public void offsetRaw(float deltaX, float deltaY) {
        rawX += deltaX;
        rawY += deltaY;
    }

    /**
     * 设置绘制坐标
     *
     * @param x
     * @param y
     */
    @Override
    public void set(float x, float y) {
        locX = x;
        locY = y;
    }

    /**
     * 设置计算坐标
     *
     * @param x
     * @param y
     */
    @Override
    public void setRaw(float x, float y) {
        rawX = x;
        rawY = y;
    }

    /**
     * 设置真实坐标
     *
     * @param x
     * @param y
     */
    @Override
    public void setReal(float x, float y) {
        realX = x;
        realY = y;
        updateRaw();
    }

    /**
     * 设置比例尺
     * <p>
     * 平面图坐标与实际坐标的比值
     * </p>
     *
     * @param scale
     */
    @Override
    public void setScale(float scale) {
        this.scale = scale;
        //更新
        updateRaw();
    }

    private void updateRaw() {
        rawX = realX / scale;
        rawY = realY / scale;
    }
}
