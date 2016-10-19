package com.feifan.planlib.layer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.planlib.PlanOrigin;
import com.feifan.planlib.IOperableLayer;
import com.feifan.planlib.ILayerPoint;
import com.feifan.planlib.OnLayerListener;
import com.feifan.planlib.OnOperationCallback;
import com.feifan.planlib.OnOperationCallback.PointInfo;
import com.feifan.planlib.entity.IPlanPointImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * 平面图标记层
 * Created by xuchunlei on 16/8/11.
 */
public class MarkLayer implements IOperableLayer {

    // 标记点
    private List<IPlanPointImpl> marks = new ArrayList<>();
    private List<IPlanPointImpl> pendingMarks;
    private long lockFlags = -1l;      // bit位均为1，表示所有的点均被锁定锁定，支持64个标记点

    // 标记点位图
    private Bitmap bmpMark;
    private float markWidth;
    private float markHeight;

    // 缩放因子
    private float lastScale; // 最后使用的缩放因子
    private float planScale; // 平面图比例尺

    // 文字画笔
    private Paint mPaint = new Paint();

    // 图层变化监听者
    private OnLayerListener mListener;
    private OnOperationCallback mCallback;

    public MarkLayer() {
        mPaint.setTextSize(28);
        this.lastScale = 1f;
    }

    public void setMarkIcon(Bitmap bmpMark) {
        this.bmpMark = bmpMark;
        markWidth = bmpMark.getWidth() * 1.1f;
        markHeight = bmpMark.getHeight() * 1.1f;
    }

    public void setPendingData(List<IPlanPointImpl> data) {
        this.pendingMarks = data;
        // 更新状态
        lockFlags = -2l << data.size();
        LogUtils.d("lockFlags = " + Long.toBinaryString(lockFlags));
    }

    /**
     * 锁定所有标记点
     */
//    public void lockAllPoints() {
//        // 锁定所有点
//        if (marks != null && marks.size() != 0) {
//            for (IPlanPointImpl point : marks) {
//                point.setLocked(true);
//            }
//        }
//
//        // 更新锁定标记
//        lockFlags = -1l;
//    }

    /**
     * 存在未锁定的点
     * @return
     */
    public boolean hasUnLockedPoints() {
        return lockFlags != -1l;
    }

    /**
     * 图层名-用于查找
     *
     * @return
     */
    @Override
    public String getName() {
        return "mark";
    }

    @Override
    public void onInitialize(PlanOrigin origin, float planScale) {
        this.planScale = planScale;
        if(pendingMarks != null && pendingMarks.size() != 0) {
            for(IPlanPointImpl point : pendingMarks) {
                point.setDraw(point.getRawX() + origin.getX(), point.getRawY() + origin.getY());
                marks.add(point);
            }
            pendingMarks.clear();
        }
    }

    @Override
    public void onMoveEvent(LayerEvent event) {
        if (marks != null && marks.size() != 0) {
            for (IPlanPointImpl point : marks) {
                if (!point.isMovable()) { // 移动后的显示位置
                    point.offset(event.x, event.y);
                } else { // 移动后的实际位置
                    point.offsetRaw(-event.x / event.scale, -event.y / event.scale);
                }
            }
        }
        if(event.action == LayerEvent.ACTION_END) {
            mCallback.onAdjustEnd();
        }
    }

    @Override
    public void onScaleEvent(LayerEvent event, PlanOrigin origin) {
            lastScale *= event.scale;
            if(marks != null && marks.size() != 0) {
                for(IPlanPointImpl point : marks) {
                    if(!point.isMovable()) {   // 缩放后的显示位置
                        float x = (point.getLocX() - event.x) * event.scale + event.x;
                        float y = (point.getLocY() - event.y) * event.scale + event.y;
                        point.setDraw(x, y);
                    } else {                 // 缩放后的坐标
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
            for (IPlanPointImpl point : marks) {
                if (bmpMark != null) {
                    canvas.drawBitmap(bmpMark,
                            point.getLocX() - bmpMark.getWidth() * 0.5f,
                            point.getLocY() - bmpMark.getHeight(), null);
                    canvas.drawText(point.toString(), point.getLocX() - bmpMark.getWidth(),
                            point.getLocY() + bmpMark.getHeight(), mPaint);
                }
            }
        }
    }

    @Override
    public void setOnLayerListener(OnLayerListener listener) {
        mListener = listener;
    }

    @Override
    public void setCallback(OnOperationCallback callback) {
        mCallback = callback;
    }

    /**
     * 通过触碰点坐标查找标记点
     * @param x 触碰点横坐标
     * @param y 触碰点纵坐标
     * @return 存在则返回标记点,否则返回null
     */
    @Override
    public ILayerPoint findPointByTouch(float x, float y) {
        if(marks != null) {
            for(IPlanPointImpl mark : marks) {
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
    public ILayerPoint add(float x, float y, float rx, float ry) {

        ILayerPoint point = null;
        if(mCallback != null) { // 在外部创建坐标点
            PointInfo info = new PointInfo(rx, ry);
            point = mCallback.onCreatePoint(info);
            if(point != null) {
                point.setDraw(x, y);
                point.setScale(planScale);
            }
        } else { //在内部创建坐标点
            point = new IPlanPointImpl(x, y, rx, ry, planScale);
            // FIXME: 16/9/27 id maybe equal while delete some points before
            point.setId(marks.size() + 1);
        }

        if(point != null && point instanceof IPlanPointImpl) {
            marks.add((IPlanPointImpl) point);
            // 刷新平面图
            if(mListener != null) {
                mListener.notifyLayerDataChanged();
            }
            LogUtils.i("add mark(" + x + "," + y + ") successfully.");
        }

        // lockFlags对应位置位设置为0
//        long flag = 1l << (marks.size() - 1);
//        lockFlags ^= flag;
//        LogUtils.d("mark:add at " + marks.size() + " position, lockFlags = " + Long.toBinaryString(lockFlags));

        return point;
    }

    @Override
    public boolean remove(ILayerPoint point) {
        boolean ret;
        int index = marks.indexOf(point);
        if(index != -1) {
            lockFlags ^= ~(1l << index);
            lockFlags = ~lockFlags;
            LogUtils.i("remove " + point.toString() + " successfully");
            LogUtils.d("mark:remove at " + index + " position, lockFlags = " + lockFlags);
        }
        if(mCallback != null) {  // 在外部移除点
            ret = mCallback.onDeletePoint(point);
            if(!ret) {
                LogUtils.w("delete mark(" + point.getRawX() + "," + point.getRawY() + ") failed outside!");
                return false;
            }
        }

        ret = marks.remove(point);
        LogUtils.i((ret ? "succeed" : "fail") + " to remove " + point.toString());
        return ret;
    }
}
