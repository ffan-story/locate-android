package com.feifan.locate.widget.plan;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;

import com.feifan.locate.utils.LogUtils;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 平面图标记层
 * Created by xuchunlei on 16/8/11.
 */
public class MarkLayer implements IPlanLayer {

    private static final String TAG = "MarkerLayer";

    // 标记点
    private List<MarkPoint> marks;
    private long lockFlags = -1l;      // bit位均为1，表示所有的点均被锁定锁定，支持64个标记点

    // 标记点位图
    private Bitmap bmpMark;
    private float markWidth;
    private float markHeight;

    // 最后使用的缩放因子
    private float lastScale;

    // 文字画笔
    private Paint mPaint = new Paint();

    public MarkLayer() {
        mPaint.setTextSize(28);
        this.lastScale = 1f;
    }

    public void setMarkIcon(Bitmap bmpMark) {
        this.bmpMark = bmpMark;
        markWidth = bmpMark.getWidth() * 1.1f;
        markHeight = bmpMark.getHeight() * 1.1f;
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
     * @param ox 平面图原点横坐标
     * @param oy 平面图原点纵坐标
     */
    public void addMark(float x, float y, float ox, float oy, float scale) {
        if(marks == null) {
            marks = new ArrayList<>();
        }
        if(x >= ox && y >= oy) {     // image与view的间距处不能标记
            marks.add(new MarkPoint(x, y, (x - ox) / scale, (y - oy) / scale));
        }

        // lockFlags对应位置位设置为0
        long flag = 1l << (marks.size() - 1);
        lockFlags ^= flag;

        LogUtils.d("add mark(" + x + "," + y + ") successfully.");
        // temp
//        lockAllPoints();
    }

    public boolean removeMark(MarkPoint mark) {
        int index = marks.indexOf(mark);
        Log.e("Mark", "index-->" + index);
        return marks.remove(mark);
    }

    /**
     * 存在未锁定的点
     * @return
     */
    public boolean hasUnLockedPoints() {
        return lockFlags != -1l;
    }

    @Override
    public void onMoveEvent(float transX, float transY, float scale) {
        if (marks != null && marks.size() != 0) {
            for (MarkPoint point : marks) {
                if (point.isLocked()) { // 移动后的显示位置
                    point.mShowLoc.offset(transX, transY);
                } else { // 移动后的实际位置
                    point.mOriginLoc.offset(-transX / scale, -transY / scale);
                }
            }
        }
    }

    @Override
    public void onScaleEvent(float scale, float focusX, float focusY, float originX, float originY) {
            lastScale *= scale;
            if(marks != null && marks.size() != 0) {
                for(MarkPoint point : marks) {
                    if(point.isLocked()) {   // 缩放后的显示位置
                        float x = (point.getShowLocX() - focusX) * scale + focusX;
                        float y = (point.getShowLocY() - focusY) * scale + focusY;
                        point.mShowLoc.set(x, y);
                    } else {                // 缩放后的坐标
                        float x = (point.getShowLocX() - originX) / lastScale;
                        float y = (point.getShowLocY() - originY) / lastScale;
                        point.mOriginLoc.set(x, y);
                    }
                }
            }
    }

    @Override
    public void onDraw(Canvas canvas) {
            if(marks != null && marks.size() != 0) {
                for(MarkPoint point : marks) {
                    if(bmpMark != null) {
                        canvas.drawBitmap(bmpMark,
                                point.getShowLocX() - bmpMark.getWidth() * 0.5f,
                                point.getShowLocY() - bmpMark.getHeight(), null);

                        canvas.drawText(point.toString(), point.getShowLocX() - bmpMark.getWidth(), point.getShowLocY() + bmpMark.getHeight(), mPaint);
                    }
                }
            }
    }

    /**
     * 通过触碰点坐标查找标记点
     * @param x 触碰点横坐标
     * @param y 触碰点纵坐标
     * @return 存在则返回标记点,否则返回null
     */
    public MarkPoint findMarkByTouchPoint(float x, float y) {
//        Log.e("findMarkByTouchPoint", "x,y-->" + x + "," + y );
        if(marks != null) {
            for(MarkPoint mark : marks) {
                float left = mark.getShowLocX() - markWidth * 0.5f;
                float right = mark.getShowLocX() + markWidth * 0.5f;
                float top = mark.getShowLocY() - markHeight;
                float bottom = mark.getShowLocY();

                Log.e("findMarkByTouch", "x,y-->" + x + "," + y + "##l,r,t,b-->" + left + "," + top + "," + right + "," + bottom);

                if(x >= left && x < right && y >= top && y < bottom) {
                    return mark;
                }
            }
        }
        return null;
    }

    /**
     * 标记点
     * <pre>
     *     记录标记点的位置和区域信息
     * </pre>
     */
    public static class MarkPoint implements Parcelable {

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

        public MarkPoint(Parcel in) {
            mOriginLoc = in.readParcelable(PointF.class.getClassLoader());
            mShowLoc = in.readParcelable(PointF.class.getClassLoader());
            mLocked = in.readByte() != 0;
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

        public boolean isLocked() {
            return mLocked;
        }

        @Override
        public boolean equals(Object o) {

            if(o instanceof MarkPoint) {
                MarkPoint point = (MarkPoint)o;
                return point.getOriginX() == getOriginX() && point.getOriginY() == getOriginY();
            }

            return false;
        }

        @Override
        public int hashCode() {
            return mOriginLoc.hashCode();
        }

        @Override
        public String toString() {
            return String.format("(%.2f, %.2f)", mOriginLoc.x, mOriginLoc.y);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(mOriginLoc, flags);
            dest.writeParcelable(mShowLoc, flags);
            dest.writeByte((byte)(mLocked ? 1 : 0));
        }

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

            @Override
            public Object createFromParcel(Parcel source) {
                return new MarkPoint(source);
            }

            @Override
            public Object[] newArray(int size) {
                return new MarkPoint[size];
            }
        };
    }
}
