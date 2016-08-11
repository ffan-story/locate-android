package com.feifan.locate.widget.plan;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

import com.feifan.locate.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 平面图标记层
 * Created by xuchunlei on 16/8/11.
 */
public class MarkerLayer {

    // 标记点列表
    private List<PointF> marks;

    // 标记点位图
    private Bitmap bmpMark;

    public MarkerLayer() {
        addMark(100, 100);
        addMark(200, 200);
    }

    /**
     * 处理触控事件
     * <pre>
     *     在平面图上添加和移动标记
     * </pre>
     * @param event
     */
    public void onTouchEvent(MotionEvent event) {


    }

    public void draw(Canvas canvas) {
        if(marks != null && marks.size() != 0) {
            for(PointF point : marks) {
                if(bmpMark != null)
                canvas.drawBitmap(bmpMark, point.x - bmpMark.getWidth() / 2,
                        point.y - bmpMark.getHeight() / 2, null);
            }
        }
    }

    public void setMark(Bitmap bmpMark) {
        this.bmpMark = bmpMark;
    }

    // 添加标记
    private void addMark(float x, float y) {
        if(marks == null) {
            marks = new ArrayList<>();
        }
        marks.add(new PointF(x, y));
    }
}
