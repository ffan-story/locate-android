package com.feifan.planlib.layer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.planlib.ILayerPoint;
import com.feifan.planlib.IOperableLayer;
import com.feifan.planlib.OnLayerListener;
import com.feifan.planlib.OnOperationCallback;
import com.feifan.planlib.PlanOrigin;
import com.feifan.planlib.entity.IPlanPointImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bianying on 2016/10/16.
 */

public abstract class OperablePointLayer<P extends ILayerPoint> implements IOperableLayer {

    // draw
    private OnOperationCallback mCallback;
    protected List<P> mPoints = new ArrayList<>();
    private List<P> mPendingData;                // 预加载数据
    private OnLayerListener mListener;
    private Paint mPaint = new Paint();

    // icon
    protected Bitmap mIcon;
    private float iconWidth;
    private float iconHeight;

    // scale
    private float lastScale; // 最后使用的缩放因子
    private float mPlanScale; // 平面图比例尺

    public OperablePointLayer() {
        mPaint.setTextSize(28);
        this.lastScale = 1f;
    }

    public void setIcon(Bitmap icon) {
        mIcon = icon;
        iconWidth = icon.getWidth() * 1.1f;
        iconHeight = icon.getHeight() * 1.1f;
    }

    public void setPendingData(List<P> data) {
        mPendingData = data;
    }

    @Override
    public void setCallback(OnOperationCallback callback) {
        mCallback = callback;
    }

    @Override
    public void setOnLayerListener(OnLayerListener listener) {
        mListener = listener;
    }

    @Override
    public void onInitialize(PlanOrigin origin, float planScale) {
        mPlanScale = planScale;
        if(mPendingData != null && mPendingData.size() != 0) {
            for(P point : mPendingData) {
                point.setDraw(point.getRawX() + origin.getX(), point.getRawY() + origin.getY());
                mPoints.add(point);
            }
            mPendingData.clear();
        }
    }

    @Override
    public ILayerPoint add(float x, float y, float rx, float ry) {
        P point;
        if(mCallback != null) { // 在外部创建坐标点
            OnOperationCallback.PointInfo info = new OnOperationCallback.PointInfo(rx, ry);
            point = (P)mCallback.onCreatePoint(info);
            if(point != null) {
                point.setDraw(x, y);
                point.setScale(mPlanScale);
            }
        } else { //在内部创建坐标点
            point = (P)new IPlanPointImpl(x, y, rx, ry, mPlanScale);
            point.setId(generateIdInternal());
        }

        if(point != null) {
            mPoints.add(point);
            // 刷新平面图
            if(mListener != null) {
//                mListener.notifyLayerDataChanged(point);
                mListener.notifyLayerDataChanged();
            }
            LogUtils.i("add an operable point(" + x + "," + y + ") successfully.");
        }

        return point;
    }

    @Override
    public boolean remove(ILayerPoint point) {
        boolean ret;
        if(mCallback != null) {  // 在外部移除点
            ret = mCallback.onDeletePoint(point);
            if(!ret) {
                LogUtils.w("delete point(" + point.getRawX() + "," + point.getRawY() + ") failed outside!");
                return false;
            }
        }

        ret = mPoints.remove(point);
        // 刷新平面图
        if(mListener != null) {
//                mListener.notifyLayerDataChanged(point);
            mListener.notifyLayerDataChanged();
        }
        LogUtils.i((ret ? "succeed" : "fail") + " to remove " + point.toString());
        return ret;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mPoints != null && mPoints.size() != 0) {
            for (P point : mPoints) {
                if (mIcon != null) {
                    canvas.drawBitmap(mIcon,
                            point.getLocX() - mIcon.getWidth() * 0.5f,
                            point.getLocY() - mIcon.getHeight(), null);
                    canvas.drawText(point.toString(), point.getLocX() - mIcon.getWidth(),
                            point.getLocY() + mIcon.getHeight(), mPaint);
                    drawOthers(canvas, point);
                }
            }
        }
    }

    @Override
    public void onScaleEvent(LayerEvent event, PlanOrigin origin) {
        lastScale *= event.scale;
        if(mPoints != null && mPoints.size() != 0) {
            for(ILayerPoint point : mPoints) {
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
    public void onMoveEvent(LayerEvent event) {
        if (mPoints != null && mPoints.size() != 0) {
            for (ILayerPoint point : mPoints) {
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
    public ILayerPoint findPointByTouch(float x, float y) {
        if(mPoints != null) {
            for(P point : mPoints) {
                float left = point.getLocX() - iconWidth;
                float right = point.getLocX() + iconWidth;
                float top = point.getLocY() - iconHeight;
                float bottom = point.getLocY();

                if(x >= left && x < right && y >= top && y < bottom) {
                    return point;
                }
            }
        }
        return null;
    }

    /**
     * 生成内部使用的点ID
     * @return
     */
    protected int generateIdInternal() {
        throw new UnsupportedOperationException("you should implement this method first");
    }

    protected void drawOthers(Canvas canvas, P point) {

    }
}
