package com.feifan.locate.widget.plan;

import android.graphics.Canvas;

/**
 * 平面图层接口
 * <pre>
 *     定义图层操作
 * </pre>
 * Created by xuchunlei on 16/8/19.
 */
public interface IPlanLayer {

    /**
     * 平移事件
     * @param transX 横轴平移距离
     * @param transY 纵轴平移距离
     * @param scale  缩放因子
     */
    void onMoveEvent(float transX, float transY, float scale);

    /**
     * 缩放事件
     * @param scale    缩放因子
     * @param focusX   缩放中心点横坐标
     * @param focusY   缩放中心点纵坐标
     * @param originX  缩放原点横坐标
     * @param originY  缩放原点纵坐标
     */
    void onScaleEvent(float scale, float focusX, float focusY, float originX, float originY);

    /**
     * 绘制事件
     * @param canvas 画布
     */
    void onDraw(Canvas canvas);
}
