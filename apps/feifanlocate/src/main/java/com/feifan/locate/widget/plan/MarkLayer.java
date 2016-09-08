package com.feifan.locate.widget.plan;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.BuildConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 平面图标记层
 * Created by xuchunlei on 16/8/11.
 */
public class MarkLayer implements IPlanLayer.IOperablePlanLayer {

    private static final String TAG = "MarkerLayer";

    // 标记点
    private List<MarkPoint> marks = new ArrayList<>();
    private List<MarkPoint> pendingMarks;
    private long lockFlags = -1l;      // bit位均为1，表示所有的点均被锁定锁定，支持64个标记点

    // 标记点位图
    private Bitmap bmpMark;
    private float markWidth;
    private float markHeight;

    // 最后使用的缩放因子
    private float lastScale;

    // 文字画笔
    private Paint mPaint = new Paint();

    // 图层变化监听者
    private OnPlanLayerListener mListener;
    private OnOperationListener mOperationListener;

    public MarkLayer() {
        mPaint.setTextSize(28);
        this.lastScale = 1f;
    }

    public void setMarkIcon(Bitmap bmpMark) {
        this.bmpMark = bmpMark;
        markWidth = bmpMark.getWidth() * 1.1f;
        markHeight = bmpMark.getHeight() * 1.1f;
    }

    public void setPlanLayerListener(OnPlanLayerListener listener) {
        this.mListener = listener;
    }

    public void setPendingData(List<MarkPoint> data) {
        this.pendingMarks = data;
        // 更新状态
        lockFlags = -2l << data.size();
        LogUtils.d("lockFlags = " + Long.toBinaryString(lockFlags));

//        if(mListener != null) {
//            mListener.notifyLayerDataChanged();
//        }
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
     * 存在未锁定的点
     * @return
     */
    public boolean hasUnLockedPoints() {
        return lockFlags != -1l;
    }

    @Override
    public void onInitialize(PlanOrigin origin) {
        if(pendingMarks != null && pendingMarks.size() != 0) {
            for(MarkPoint point : pendingMarks) {
                point.set(point.getRawX() + origin.getX(), point.getRawY() + origin.getY());
                marks.add(point);
            }
        }
        pendingMarks.clear();
    }

    @Override
    public void onMoveEvent(LayerEvent event) {
        if (marks != null && marks.size() != 0) {
            for (MarkPoint point : marks) {
                if (point.isLocked()) { // 移动后的显示位置
                    point.offset(event.x, event.y);
                } else { // 移动后的实际位置
                    point.offsetRaw(-event.x / event.scale, -event.y / event.scale);
                }
            }
        }
    }

    @Override
    public void onScaleEvent(LayerEvent event, PlanOrigin origin) {
            lastScale *= event.scale;
            if(marks != null && marks.size() != 0) {
                for(MarkPoint point : marks) {
                    if(point.isLocked()) {   // 缩放后的显示位置
                        float x = (point.getLocX() - event.x) * event.scale + event.x;
                        float y = (point.getLocY() - event.y) * event.scale + event.y;
                        point.set(x, y);
                    } else {                // 缩放后的坐标
                        float x = (point.getLocX() - origin.getX()) / lastScale;
                        float y = (point.getLocY() - origin.getY()) / lastScale;
                        point.setRaw(x, y);
                    }
                }
            }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (marks != null && marks.size() != 0) {
            for (MarkPoint point : marks) {
                if (bmpMark != null) {
                    canvas.drawBitmap(bmpMark,
                            point.getLocX() - bmpMark.getWidth() * 0.5f,
                            point.getLocY() - bmpMark.getHeight(), null);
                    canvas.drawText(point.toString(), point.getLocX() - bmpMark.getWidth(), point.getLocY() + bmpMark.getHeight(), mPaint);
                }
            }
        }
    }

    @Override
    public void setOnPlanLayerListener(OnPlanLayerListener listener) {
        mListener = listener;
    }

    public void setOperationListener(OnOperationListener listener) {
        mOperationListener = listener;
    }

    /**
     * 通过触碰点坐标查找标记点
     * @param x 触碰点横坐标
     * @param y 触碰点纵坐标
     * @return 存在则返回标记点,否则返回null
     */
    @Override
    public IPlanPoint findPointByTouch(float x, float y) {
        if(marks != null) {
            for(MarkPoint mark : marks) {
                float left = mark.getLocX() - markWidth;
                float right = mark.getLocX() + markWidth;
                float top = mark.getLocY() - markHeight;
                float bottom = mark.getLocY();

                if(x >= left && x < right && y >= top && y < bottom) {
                    return mark;
                }
            }
        }
        return null;
    }

    @Override
    public IPlanPoint getActivePoint() {
//        int index =
        return null;
    }

    @Override
    public IPlanPoint add(float x, float y, float rx, float ry) {
        MarkPoint mark;
        mark = new MarkPoint(x, y, rx, ry);
        marks.add(mark);

        // lockFlags对应位置位设置为0
        long flag = 1l << (marks.size() - 1);
        lockFlags ^= flag;
        LogUtils.i("add mark(" + x + "," + y + ") successfully.");
        if(BuildConfig.DEBUG) {
            LogUtils.d("mark:add at " + marks.size() + " position, lockFlags = " + Long.toBinaryString(lockFlags));
        }

        if(mListener != null) {
            mListener.notifyLayerDataChanged();
        }
        if(mOperationListener != null) {
            mOperationListener.onCreatePoint(mark);
        }
        return mark;
    }

    @Override
    public boolean remove(IPlanPoint point) {
        int index = marks.indexOf(point);
        if(index != -1) {
            lockFlags ^= ~(1l << index);
            lockFlags = ~lockFlags;
            LogUtils.i("remove " + point.toString() + " successfully");
            LogUtils.d("mark:remove at " + index + " position, lockFlags = " + lockFlags);
        }
        boolean ret = marks.remove(point);
        if(mOperationListener != null) {
            mOperationListener.onDeletePoint(point);
        }
        LogUtils.i((ret ? "succeed" : "fail") + " to remove " + point.toString());
        return ret;
    }

    /**
     * 标记点
     * <pre>
     *     记录标记点的位置和区域信息
     * </pre>
     */
    public static class MarkPoint implements IPlanPoint, Parcelable {

        /** 计算位置 */
        private PointF mRawLoc;

        /** 绘制位置 */
        private PointF mLoc;

        /** 锁定，锁定时不能被移动 */
        private boolean mLocked;

        public MarkPoint(float x, float y, float rx, float ry) {
            mLoc = new PointF(x, y);
            mRawLoc = new PointF(rx, ry);
        }

        public MarkPoint(Parcel in) {
            mRawLoc = in.readParcelable(PointF.class.getClassLoader());
            mLoc = in.readParcelable(PointF.class.getClassLoader());
            mLocked = in.readByte() != 0;
        }

        @Override
        public void offset(float deltaX, float deltaY) {
            mLoc.offset(deltaX, deltaY);
        }

        @Override
        public void offsetRaw(float deltaX, float deltaY) {
            mRawLoc.offset(deltaX, deltaY);
        }

        @Override
        public void set(float x, float y) {
            mLoc.set(x, y);
        }

        @Override
        public void setRaw(float x, float y) {
            mRawLoc.set(x, y);
        }

        /**
         * 获取显示位置横坐标
         * @return
         */
        public float getLocX() {
            return mLoc != null ? mLoc.x : 0;
        }

        /**
         * 获取显示位置纵坐标
         * @return
         */
        public float getLocY() {
            return mLoc != null ? mLoc.y : 0;
        }

        /**
         * 获取原始位置横坐标
         * @return
         */
        @Override
        public float getRawX() {
            return mRawLoc != null ? mRawLoc.x : 0;
        }

        /**
         * 获取原始位置纵坐标
         * @return
         */
        @Override
        public float getRawY() {
            return mRawLoc != null ? mRawLoc.y : 0;
        }

        public boolean isLocked() {
            return mLocked;
        }

        @Override
        public boolean equals(Object o) {

            if(o instanceof MarkPoint) {
                MarkPoint point = (MarkPoint)o;
                return point.getRawX() == getRawX() && point.getRawY() == getRawY();
            }

            return false;
        }

        @Override
        public int hashCode() {
            return mRawLoc.hashCode();
        }

        @Override
        public String toString() {
            return String.format("(%.2f, %.2f)", mRawLoc.x, mRawLoc.y);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(mRawLoc, flags);
            dest.writeParcelable(mLoc, flags);
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
