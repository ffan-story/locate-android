package com.feifan.maplib.entity;

import com.rtm.frm.data.Location;
import com.rtm.frm.data.Point;

/**
 * Created by xuchunlei on 2016/10/25.
 */

public interface ILayerPoint {

    /**
     * 设置索引编号
     * @param id
     */
    void setId(int id);
    /**
     * 获取索引编号
     * @return
     */
    int getId();

    /**
     * 设置绘制位置坐标
     * @param x
     * @param y
     */
    void setDraw(float x, float y);

    /**
     * 获取绘制点
     * @return
     */
    Point getDraw();

    /**
     * 设置实际位置
     * @param l
     */
    void setLocation(Location l);

    /**
     * 获取实际位置
     * @return
     */
    Location getLocation();

    /**
     * 是否可移动
     * @return
     */
    boolean isMovable();

    /**
     * 设置移动标记
     * @param movable
     */
    void setMovable(boolean movable);

    /**
     * 是否为孤立点
     * @return
     */
    boolean isIsolated();
}
