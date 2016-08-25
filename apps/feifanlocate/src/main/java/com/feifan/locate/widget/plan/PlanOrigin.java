package com.feifan.locate.widget.plan;

/**
 * 平面图原点
 *
 * Created by xuchunlei on 16/8/19.
 */
public class PlanOrigin {

    // 内容层原点坐标
    private float originX;
    private float originY;

    public PlanOrigin(float x, float y) {
        originX = x;
        originY = y;
    }

    /**
     * 移动事件
     * @param transX
     * @param transY
     */
    public void onMoveEvent(float transX, float transY) {
        originX += transX;
        originY += transY;
    }

    /**
     * 缩放事件
     * @param scale
     * @param focusX
     * @param focusY
     */
    public void onScaleEvent(float scale, float focusX, float focusY) {
        originX = (originX - focusX) * scale + focusX;
        originY = (originY - focusY) * scale + focusY;
    }

    public float getOriginX() {
        return originX;
    }

    public float getOriginY() {
        return originY;
    }
}
