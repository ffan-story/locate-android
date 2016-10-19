package com.feifan.planlib.layer;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.feifan.planlib.ILayer;
import com.feifan.planlib.OnLayerListener;
import com.feifan.planlib.PlanOrigin;
import com.feifan.planlib.entity.AutoPoint;
import com.feifan.planlib.entity.LayerPoint;

/**
 * 跟踪轨迹图层
 * <p>
 *     支持绘制位置,并描绘各点之间的轨迹
 * </p>
 *
 * Created by xuchunlei on 16/9/23.
 */

public class TraceLayer implements ILayer {

    // draw
    private AutoPoint mCurrentPoint = new AutoPoint();
    private Bitmap mBmp;
    private float mPlanScale;
    private PlanOrigin mOrigin;

    // translate & scale
    private float mLastScale = 1f;
    private float mTranX;
    private float mTranY;

    // listener
    private OnLayerListener mListener;

    /**
     * 图层名-用于查找
     *
     * @return
     */
    @Override
    public String getName() {
        return "trace";
    }

    /**
     * image坐标系原点
     *
     * @param origin
     * @param planScale
     */
    @Override
    public void onInitialize(PlanOrigin origin, float planScale) {
        mPlanScale = planScale;
        mOrigin = origin;

        mCurrentPoint.setScale(mPlanScale);
        mListener.notifyLayerDataChanged(); // 重新绘制
    }

    /**
     * 平移事件
     *
     * @param event x表示横轴平移距离，y表示纵轴平移距离，scale表示缩放因子
     */
    @Override
    public void onMoveEvent(LayerEvent event) {
        mCurrentPoint.offset(event.x, event.y);
    }

    /**
     * 缩放事件
     *
     * @param event  x表示缩放中心横坐标，y表示缩放中心纵坐标，scale表示缩放因子
     * @param origin x表示平面图原点横坐标，y表示平面图原点纵坐标
     */
    @Override
    public void onScaleEvent(LayerEvent event, PlanOrigin origin) {
        mLastScale *= event.scale;
        float x = (mCurrentPoint.getLocX() - event.x) * event.scale + event.x;
        float y = (mCurrentPoint.getLocY() - event.y) * event.scale + event.y;
        mCurrentPoint.setDraw(x, y);
    }

    /**
     * 绘制事件
     *
     * @param canvas 画布
     */
    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBmp, mCurrentPoint.getLocX() - mBmp.getWidth() * 0.5f,
                mCurrentPoint.getLocY() - mBmp.getHeight(), null);
    }

    /**
     * 图层监听
     *
     * @param listener
     */
    @Override
    public void setOnLayerListener(OnLayerListener listener) {
        this.mListener = listener;
    }

    public void setDrawBitmap(Bitmap bmp) {
        this.mBmp = bmp;
    }

    public void drawTracePoint(float x, float y) {
        mCurrentPoint.setReal(x, y);

        if(mOrigin != null) { // 初始化完成

            // 绘制坐标
            float locX = mCurrentPoint.getRawX() * mLastScale + mOrigin.getX();
            float locY = mCurrentPoint.getRawY() * mLastScale + mOrigin.getY();
            mCurrentPoint.setDraw(locX, locY);

            mListener.notifyLayerDataChanged();
        }
    }
}
