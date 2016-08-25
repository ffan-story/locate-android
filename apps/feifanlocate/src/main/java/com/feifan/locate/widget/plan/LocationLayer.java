package com.feifan.locate.widget.plan;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

/**
 * 定位层
 * <pre>
 *     展示方位
 * </pre>
 * Created by xuchunlei on 16/8/24.
 */
public class LocationLayer implements IPlanLayer {

    // 当前位置
    private PointF mLocation;

    public void setLocation(PointF location) {
        this.mLocation = location;
    }

    @Override
    public void onMoveEvent(float transX, float transY, float scale) {

    }

    @Override
    public void onScaleEvent(float scale, float focusX, float focusY, float originX, float originY) {

    }

    @Override
    public void onDraw(Canvas canvas) {

    }
}
