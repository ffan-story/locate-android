package com.feifan.locate.widget.plan;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.feifan.locate.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 平面图标记层
 * Created by xuchunlei on 16/8/11.
 */
public class MarkerLayer {

    private static final String TAG = "MarkerLayer";

    // 标记点
    private List<MarkPoint> marks;
    private long lockFlags = -1l;      // bit位均为1，表示所有的点均被锁定锁定，支持64个标记点

    // 标记点位图
    private Bitmap bmpMark;

    // 文字画笔
    private Paint mPaint = new Paint();

    public MarkerLayer() {
        mPaint.setTextSize(32);
    }

    /**
     * 处理缩放
     * @param scale 缩放系数
     * @param focusX 横轴缩放中心横坐标
     * @param focusY 纵轴缩放中心纵坐标
     */
    public void onScaleEvent(float scale, float focusX, float focusY) {
        if(marks != null && marks.size() != 0) {
            for(MarkPoint point : marks) {
                if(point.isLocked()) {   // 缩放后的显示位置
                    float x = (point.getShowLocX() - focusX) * scale + focusX;
                    float y = (point.getShowLocY() - focusY) * scale + focusY;
                    point.mShowLoc.set(x, y);
                }else {         // 缩放后的实际位置
                    float x = point.getOriginX() - (1 - scale) * focusX;
                    float y = point.getOriginY() - (1 - scale) * focusY;
                    point.mOriginLoc.set(x, y);
                }
            }
        }
    }

    /**
     * 处理平移
     * @param offsetX
     * @param offsetY
     */
    public void onMoveEvent(float offsetX, float offsetY, float scale) {
        if(marks != null && marks.size() != 0) {
            for(MarkPoint point : marks) {
                if(point.isLocked()) { // 移动后的显示位置
                    point.mShowLoc.offset(offsetX, offsetY);
                }else { // 移动后的实际位置
                    point.mOriginLoc.offset(-offsetX / scale, -offsetY / scale);
                }
            }
        }
    }

    public void draw(Canvas canvas) {
        if(marks != null && marks.size() != 0) {
            for(MarkPoint point : marks) {
                if(bmpMark != null) {
                    canvas.drawBitmap(bmpMark, point.getShowLocX() - bmpMark.getWidth() / 2,
                            point.getShowLocY() - bmpMark.getHeight(), null);
                    canvas.drawText(point.toString(), point.getShowLocX() - 10, point.getShowLocY() + 10, mPaint);
                }
            }
        }
    }

    public void setMarkIcon(Bitmap bmpMark) {
        this.bmpMark = bmpMark;
    }

    /**
     * 锁定所有标记点
     */
    public void lockAllPoints() {
        // 锁定所有点
        if (marks != null && marks.size() != 0) {
            for (MarkPoint point : marks) {
                point.mLocked = true;
            }
        }

        // 更新锁定标记
        lockFlags = -1l;
    }

    /**
     * 添加标记
     * @param x view中位置的横坐标
     * @param y view中位置的纵坐标
     * @param originXY  img与view原点间的平移距离
     */
    public void addMark(float x, float y, float[] originXY, float scale) {
        if(marks == null) {
            marks = new ArrayList<>();
        }
        if(x >= originXY[0] && y >= originXY[1]) {     // image与view的间距处不能标记
            marks.add(new MarkPoint(x, y, (x - originXY[0]) / scale, (y - originXY[1]) / scale));
        }

        // lockFlags对应位置位设置为0
        long flag = 1l << (marks.size() - 1);
        lockFlags ^= flag;

        // temp
//        lockAllPoints();
    }

    /**
     * 存在未锁定的点
     * @return
     */
    public boolean hasUnLockedPoints() {
        return lockFlags != -1l;
    }

    /**
     * 标记点
     * <pre>
     *     记录标记点的位置信息
     * </pre>
     */
    public static class MarkPoint {
        /** 原始位置 */
        private PointF mOriginLoc;

        /** 显示位置 */
        private PointF mShowLoc;

        /** 锁定，锁定时不能被移动 */
        private boolean mLocked;

        public MarkPoint(float sx, float sy, float ox, float oy) {
            mOriginLoc = new PointF(ox, oy);
            mShowLoc = new PointF(sx, sy);
        }

        /**
         * 获取显示位置横坐标
         * @return
         */
        public float getShowLocX() {
            return mShowLoc != null ? mShowLoc.x : 0;
        }

        /**
         * 获取显示位置纵坐标
         * @return
         */
        public float getShowLocY() {
            return mShowLoc != null ? mShowLoc.y : 0;
        }

        /**
         * 获取原始位置横坐标
         * @return
         */
        public float getOriginX() {
            return mOriginLoc != null ? mOriginLoc.x : 0;
        }

        /**
         * 获取原始位置纵坐标
         * @return
         */
        public float getOriginY() {
            return mOriginLoc != null ? mOriginLoc.y : 0;
        }

        public void resetShowLoc() {
            if(mOriginLoc != null && mShowLoc != null) {
                mShowLoc.set(mOriginLoc.x, mOriginLoc.y);
            }
        }

        public void setLocked(boolean locked) {
            this.mLocked = locked;
        }

        public boolean isLocked() {
            return mLocked;
        }

        @Override
        public String toString() {
            return String.format("(%.2f, %.2f)", mOriginLoc.x, mOriginLoc.y);
        }
    }
}
