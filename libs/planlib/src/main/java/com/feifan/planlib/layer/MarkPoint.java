package com.feifan.planlib.layer;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

import com.feifan.planlib.base.ILayerPoint;

/**
 * 标记点
 * <pre>
 *     记录标记点的位置和区域信息
 * </pre>
 */
public class MarkPoint implements ILayerPoint, Parcelable{

    /** 索引 */
    private int id = -1;

    /** 计算位置 */
    private PointF mRawLoc;

    /** 绘制位置 */
    private PointF mDrawLoc;

    /** 实际位置横坐标 */
    private float x;

    /** 实际位置纵坐标 */
    private float y;

    /** 平面图比例尺 */
    private float mPlanScale = 1f;

    /** 锁定，锁定时不能被移动 */
    private boolean mLocked = false;

    /**
     * 构造方法
     * <p>
     *     用于在平面图外部创建坐标点
     * </p>
     * @param id
     * @param rx
     * @param ry
     */
    public MarkPoint(int id, float rx, float ry){
        this.id = id;
        mDrawLoc = new PointF(0, 0);
        mRawLoc = new PointF(rx, ry);
    }

    public MarkPoint(float dx, float dy, float rx, float ry, float planScale) {
        mDrawLoc = new PointF(dx, dy);
        mRawLoc = new PointF(rx, ry);
        this.mPlanScale = planScale;
        x = rx * planScale;
        y = ry * planScale;
    }

    public MarkPoint(Parcel in) {
        id = in.readInt();
        mRawLoc = in.readParcelable(PointF.class.getClassLoader());
        mDrawLoc = in.readParcelable(PointF.class.getClassLoader());
        mPlanScale = in.readFloat();
        x = in.readFloat();
        y = in.readFloat();
        mLocked = in.readByte() != 0;
    }

    @Override
    public void offset(float deltaX, float deltaY) {
        mDrawLoc.offset(deltaX, deltaY);
    }

    @Override
    public void offsetRaw(float deltaX, float deltaY) {
        mRawLoc.offset(deltaX, deltaY);
    }

    @Override
    public void set(float x, float y) {
        mDrawLoc.set(x, y);
    }

    @Override
    public void setRaw(float x, float y) {
        mRawLoc.set(x, y);
    }

    @Override
    public void setScale(float scale) {
        this.mPlanScale = scale;
    }

    /**
     * 获取显示位置横坐标
     * @return
     */
    @Override
    public float getLocX() {
        return mDrawLoc != null ? mDrawLoc.x : 0;
    }

    /**
     * 获取显示位置纵坐标
     * @return
     */
    @Override
    public float getLocY() {
        return mDrawLoc != null ? mDrawLoc.y : 0;
    }

    @Override
    public float getRealX() {
        if(!mLocked){
            x = mRawLoc.x * mPlanScale;
        }
        return x;
    }

    @Override
    public float getRealY() {
        if(!mLocked) {
            y = mRawLoc.y * mPlanScale;
        }
        return y;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
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

    public void setLocked(boolean lock) {
        mLocked = lock;
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
        return String.format("(%.2f, %.2f)", getRealX(), getRealY());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(mRawLoc, flags);
        dest.writeParcelable(mDrawLoc, flags);
        dest.writeFloat(mPlanScale);
        dest.writeFloat(x);
        dest.writeFloat(y);
        dest.writeByte((byte)(mLocked ? 1 : 0));
    }

    public static final Creator CREATOR = new Creator() {

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
