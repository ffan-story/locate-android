package com.feifan.locate.widget.plan;

/**
 * 平面图原点
 *
 * Created by xuchunlei on 16/8/19.
 */
public class PlanOrigin {

    // 内容层原点坐标
    private float x;
    private float y;

    public PlanOrigin(float x, float y) {
        set(x, y);
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 移动事件
     * @param transX
     * @param transY
     */
    public void onMoveEvent(float transX, float transY) {
        this.x += transX;
        this.y += transY;
    }

    /**
     * 缩放事件
     * @param scale
     * @param focusX
     * @param focusY
     */
    public void onScaleEvent(float scale, float focusX, float focusY) {
        x = (x - focusX) * scale + focusX;
        y = (y - focusY) * scale + focusY;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
