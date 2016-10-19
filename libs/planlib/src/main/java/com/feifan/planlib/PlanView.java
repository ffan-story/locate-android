package com.feifan.planlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.planlib.ILayer.LayerEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于平面图操作的ImageView
 * <pre>
 *     载入平面图的图片后，可进行缩放、平移、标记等操作
 * </pre>
 * Created by xuchunlei on 16/8/10.
 */
public class PlanView extends ImageView implements OnLayerListener {

    private static final String TAG = "PlanView";

    private static final int DRAW_AREA_SIZE = 100;

    // 缩放
    private ScaleGestureDetector mScaleDetector;
    private float minScale = 1;       // 最小缩小倍数
    private float maxScale = 5;       // 最大放大倍数
    private float scale = 1;          // 当前缩放倍数
    private float planScale = 1;      // 平面图比例尺

    // 点击
    private GestureDetector mGestureDetector;

    // 平移
    private PointF lastTouch = new PointF();

    // 图像变换
    private Matrix matrix = new Matrix();  // 用于图形变换的矩阵
    private float[] fixTransXY = new float[2]; // 缓存将img平移到view内的偏移量
    private float[] fixCenterXY = new float[2];  // 缓存缩放后将img平移到view中间位置的偏移量

    // 尺寸
    private int viewWidth;
    private int viewHeight;
    private float matchViewWidth; // 实际显示平面图的初始View宽度
    private float matchViewHeight;// 实际显示平面图的初始View高度

    private ILayerPoint currentPoint = null;

    // 计算
    private float[] resultXY = new float[2];
    private LayerEvent lEvent = new LayerEvent();

    @Override
    public void notifyLayerDataChanged() {
        invalidate();
    }

    @Override
    public void notifyLayerDataChanged(ILayerPoint point) {
        invalidate((int)(point.getLocX() - DRAW_AREA_SIZE),
                (int)(point.getLocX() + DRAW_AREA_SIZE),
                (int)(point.getLocY() - DRAW_AREA_SIZE),
                (int)(point.getLocY() + DRAW_AREA_SIZE));
    }

    // 操作状态
    private enum State {
        NONE, // 不进行任何操作时的状态
        ZOOM, // 缩放状态
        DRAG, // 拖拽状态
    }
    private State mState;
    private boolean mLimited = false; // 平移是否限制在view内

    // 用于获取matrix的参数
    private float[] m = new float[9];
    // 平面图原点坐标
    private PlanOrigin mPlanOrigin = new PlanOrigin(0, 0);

    // 图层
    private List<ILayer> mLayers;
    private Map<String, ILayer> mLayerMap;
    private IOperableLayer mOperableLayer;       // 可控图层，只能同时操控一个可控图层
    private OnPlanListener mPlanListener;
    private boolean inited = false;

    public PlanView(Context context) {
        this(context, null);
    }

    public PlanView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 让GestureDetector起作用
        setClickable(true);
        mScaleDetector = new ScaleGestureDetector(context, new PlanScaleListener());
        mGestureDetector = new GestureDetector(context, new PlanGestureListener());

        setScaleType(ScaleType.MATRIX);
        setImageMatrix(matrix);
        setState(State.NONE);

        mLayers = new ArrayList<>();
        mLayerMap = new HashMap<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // 没有图片或者图片无法显示
        Drawable drawable = getDrawable();
        if(drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
            setMeasuredDimension(0, 0);
            return;
        }

        // 计算实际宽度
        int drawableWidth = drawable.getIntrinsicWidth();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        viewWidth = computeViewSize(widthMode, widthSize, drawableWidth);

        // 计算实际高度
        int drawableHeight = drawable.getIntrinsicHeight();
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        viewHeight = computeViewSize(heightMode, heightSize, drawableHeight);

        setMeasuredDimension(viewWidth, viewHeight);
        // 调整图片位置居中
        fitImageToView();

        // 初始化平面图原点
        float originX = (viewWidth - matchViewWidth) / 2;
        float originY = (viewHeight - matchViewHeight) / 2;
        mPlanOrigin.set(originX, originY);
    }

    public void setPlanScale(float planScale) {
        this.planScale = planScale;
    }

    public void addLayer(ILayer layer) {
        if(mLayerMap.containsKey(layer.getName())) {
            throw new UnsupportedOperationException("layer " + layer.getName() + " existed");
        }
        layer.setOnLayerListener(this);
        mLayers.add(layer);
        mLayerMap.put(layer.getName(), layer);

        // 可控图层
        if(layer instanceof IOperableLayer) {
            mOperableLayer = (IOperableLayer)layer;
        }
    }

    public ILayer getLayer(String name) {
        return mLayerMap.get(name);
    }

    public void setPlanListener(OnPlanListener listener) {
        mPlanListener = listener;
    }

    /**
     * 锁定平面图
     *
     */
//    public void lock() {
//        mMarkLayer.lockAllPoints();
//
//        // TODO 还原平面图原来的大小和位置，并禁止任何操作
//        translateImageToCenter();
//    }

    // 计算视图实际尺寸
    private int computeViewSize(int mode, int size, int drawableSize) {
        int viewSize;
        switch (mode) {
            case MeasureSpec.EXACTLY:      // 设置实际尺寸或者match_parent
                viewSize = size;
                break;

            case MeasureSpec.AT_MOST:      // 设置wrap_content
                viewSize = Math.min(drawableSize, size);
                break;

            case MeasureSpec.UNSPECIFIED:
                viewSize = drawableSize;
                break;

            default:
                viewSize = size;
                break;
        }
        return viewSize;
    }

    // 调整图片适应View
    private void fitImageToView() {
        Drawable drawable = getDrawable();
        if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
            return;
        }

        if(matrix == null) {
            return;
        }

        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();

        // 图片需要缩放的倍数
        float scaleX = (float) viewWidth / drawableWidth;
        float scaleY = (float) viewHeight / drawableHeight;
        float scale = Math.min(scaleX, scaleY);

        // 居中缩放
        float redundantXSpace = viewWidth - (scale * drawableWidth);
        float redundantYSpace = viewHeight - (scale * drawableHeight);
        matchViewWidth = viewWidth - redundantXSpace;
        matchViewHeight = viewHeight - redundantYSpace;

        matrix.setScale(scale, scale);
        matrix.postTranslate(redundantXSpace / 2, redundantYSpace / 2);

        fixTrans(fixTransXY);
        setImageMatrix(matrix);

    }

    // 进行边界检查，如果image小于view，则进行居中调整
    private void fixScaleTrans() {
        // 缩放之后，将img移动到view内
        fixTrans(fixTransXY);

        // img进行居中平移，虽在缩放之前，但移动的是缩放之后的距离
        matrix.getValues(m);

        // 图层偏移处理
        fixCenterXY[0] = getFixCenterTrans(m[Matrix.MTRANS_X], viewWidth, getImageWidth());
        fixCenterXY[1] = getFixCenterTrans(m[Matrix.MTRANS_Y], viewHeight, getImageHeight());
        // 偏移距离
        float transX = fixTransXY[0] + fixCenterXY[0];
        float transY = fixTransXY[1] + fixCenterXY[1];

        moveAll(transX, transY, scale);

        // img偏移处理
        if (getImageWidth() < viewWidth) { // img缩小后的宽度小于view宽度时
            m[Matrix.MTRANS_X] = (viewWidth - getImageWidth()) / 2;
        }

        if (getImageHeight() < viewHeight) { // img缩小后的高度小于view高度时
            m[Matrix.MTRANS_Y] = (viewHeight - getImageHeight()) / 2;
        }

        matrix.setValues(m);
    }

    // 进行边界检查，image位置超出view边界时，将image移动到view内部
    // return 修复过返回true，否则返回false
    private boolean fixTrans(float[] fixXY) {
        boolean ret = false;
        matrix.getValues(m);
        float transX = m[Matrix.MTRANS_X];
        float transY = m[Matrix.MTRANS_Y];

        float fixTransX = getFixTrans(transX, viewWidth, getImageWidth());
        float fixTransY = getFixTrans(transY, viewHeight, getImageHeight());

        if (fixTransX != 0 || fixTransY != 0) {
            matrix.postTranslate(fixTransX, fixTransY);
            ret = true;
        }
        if(fixXY != null && fixXY.length == 2) {
            // 更新修复偏移量
            fixXY[0] = fixTransX;
            fixXY[1] = fixTransY;
        }
        return ret;
    }

    // 获取修复后的平移距离，平移距离可以让image在view内部
    private float getFixTrans(float trans, float viewSize, float contentSize) {
        float minTrans, maxTrans;

        if (contentSize <= viewSize) { // 显示内容小于view尺寸
            minTrans = 0;
            maxTrans = viewSize - contentSize;
        } else {                       // 显示内容大于view尺寸
            minTrans = viewSize - contentSize;
            maxTrans = 0;
        }

        // 计算使image保持在view内部需要的平移量，以下注释以image比view尺寸小为例
        if (trans < minTrans)        // trans小于0时，表示image在view的左边或上边，平移至左边界或上边界重合
            return -trans + minTrans;
        if (trans > maxTrans)        // trans大于0使，表示image在view的右边或下边，平移至右边界或下边界重合
            return -trans + maxTrans;
        return 0;
    }

    /**
     * 获取img居中显示时，需要移动的距离
     * @param trans
     * @param viewSize
     * @param contentSize
     * @return
     */
    private float getFixCenterTrans(float trans, float viewSize, float contentSize) {
        if(contentSize <= viewSize) {
            return (viewSize - contentSize) / 2 - trans;
        }
        return 0;
    }

    // 图片实际宽度
    private float getImageWidth() {
        return matchViewWidth * scale;
    }
    // 图片实际高度
    private float getImageHeight() {
        return matchViewHeight * scale;
    }

    // 修复拖拽平移量，处理image尺寸小于view的情况
    private float getFixDragTrans(float delta, float viewSize, float contentSize) {
        if (contentSize <= viewSize) { // 当image的尺寸必view小时，无需平移
            return 0;
        }
        return delta;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (scale == minScale) { // 恢复初始大小允许滑动切换
            getParent().requestDisallowInterceptTouchEvent(false);
        } else {
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        // 处理缩放
        mScaleDetector.onTouchEvent(event);
        // 处理点击
        mGestureDetector.onTouchEvent(event);
        // 平移图片
        if(mState == State.NONE || mState == State.DRAG) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:          // 拖拽开始
                    if(mOperableLayer != null) {
                        currentPoint = mOperableLayer.findPointByTouch(event.getX(), event.getY());
                    }
                    lastTouch.set(event.getX(), event.getY());
                    setState(State.DRAG);
                    lEvent.action = LayerEvent.ACTION_BEGIN;
                    break;
                case MotionEvent.ACTION_MOVE:
                    lEvent.action = LayerEvent.ACTION_DOING;
                    if(currentPoint != null) {   // 手指在标记上
                        return false;
                    }
                    if(mState == State.DRAG) {
                        LogUtils.d("you are translating the image");
                        float deltaX = event.getX() - lastTouch.x;
                        float deltaY = event.getY() - lastTouch.y;

                        float fixTranX = mLimited ? deltaX : getFixDragTrans(deltaX, viewWidth, getImageWidth());
                        float fixTranY = mLimited ? deltaY : getFixDragTrans(deltaY, viewHeight, getImageHeight());
                        matrix.postTranslate(fixTranX, fixTranY);

                        if(!mLimited) {    // 不存在未锁定的点，限制img在view内
                            fixTrans(fixTransXY);
                            fixTranX += fixTransXY[0];
                            fixTranY += fixTransXY[1];
                        }

                        moveAll(fixTranX, fixTranY, scale);
                        lastTouch.set(event.getX(), event.getY());
                    }
                    break;
                case MotionEvent.ACTION_UP:            // 拖拽结束
                case MotionEvent.ACTION_POINTER_UP:
                    setState(State.NONE);
                    lEvent.action = LayerEvent.ACTION_END;
                    moveAll(0, 0, scale);
                    break;
            }
        }

        // 使缩放、平移等变换生效
        setImageMatrix(matrix);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制图层
        if(mLayers != null) {
            for(ILayer layer : mLayers) {
                if(!inited) {
                    layer.onInitialize(mPlanOrigin, planScale);
                    inited = true;
                }
                layer.onDraw(canvas);
            }
        }
    }

    // 设置操作状态
    private void setState(State state) {
        this.mState = state;
    }

    // 平移
    private void moveAll(float transX, float transY, float scale) {
        // 计算img原点
        mPlanOrigin.onMoveEvent(transX, transY);
        // 平移图层
        if(mLayers != null && mLayers.size() != 0) {
            for(ILayer layer : mLayers) {
                lEvent.x = transX;
                lEvent.y = transY;
                lEvent.scale = scale;
                layer.onMoveEvent(lEvent);
            }
        }
    }

    // 缩放
    private void scaleAll(float scale, float focusX, float focusY) {
        lEvent.x = focusX;
        lEvent.y = focusY;
        lEvent.scale = scale;
        // 更新img原点
        mPlanOrigin.onScaleEvent(scale, focusX, focusY);
        // 缩放图层
        if(mLayers != null && mLayers.size() != 0) {
            for(ILayer layer : mLayers) {
                layer.onScaleEvent(lEvent, mPlanOrigin);
            }
        }
    }

    // 平移image到view中间
    private void translateImageToCenter() {
        float focusX = getWidth() / 2;
        float focusY = getHeight() / 2;
        scaleImage(1 / scale, focusX, focusY);
        setImageMatrix(matrix);
    }

    /**
     * 缩放手势监听者
     */
    private class PlanScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            setState(State.ZOOM);
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleImage(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());

            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
            setState(State.NONE);
        }
    }

    /**
     * 普通手势监听者
     */
    private class PlanGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            final float x = e.getX();
            final float y = e.getY();

            if(currentPoint != null) {     // 点击坐标点
                LogUtils.d("Mark" + currentPoint.toString() + " is pressed");
                if(mPlanListener != null) {
                    mPlanListener.onPress(currentPoint, x, y);
                }
            }else {
                if(mOperableLayer != null) {
                    // 计算平面图坐标
                    CoordinateUtils.compute(x, y, mPlanOrigin.getX(), mPlanOrigin.getY(), scale, resultXY);
                    if(x >= mPlanOrigin.getX() && y >= mPlanOrigin.getY()) {     // 绘制点在计算坐标系之内
                        ILayerPoint point = mOperableLayer.add(x, y, resultXY[0], resultXY[1]);
                        if(point != null) {
                            currentPoint = point;
                        }
                    } else{
                        if(mPlanListener != null) {
                            mPlanListener.onNop(x, y);
                        }
                    }
                }
            }

            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            if(currentPoint != null) {
                LogUtils.d("Mark" + currentPoint.toString() + " is long-pressed");
                if(mPlanListener != null) {
                    mPlanListener.onLongPress(currentPoint, e.getX(), e.getY());
                }
                currentPoint = null;
            }
        }
    }

    /**
     * 缩放图片
     * @param deltaScale
     * @param focusX
     * @param focusY
     * @return 实际缩放倍数
     */
    private void scaleImage(double deltaScale, float focusX, float focusY) {
        // 缩放系数边界
        float originScale = scale;
        scale *= deltaScale;

        if(scale > maxScale) {
            scale = maxScale;
            deltaScale = scale / originScale;
        } else if(scale < minScale) {
            scale = minScale;
            deltaScale = scale / originScale;
        }
        matrix.postScale((float) deltaScale, (float) deltaScale, focusX, focusY);

        scaleAll((float)deltaScale, focusX, focusY);

        fixScaleTrans();
    }

    /**
     * 平面图监听接口
     */
    public interface OnPlanListener {
        /**
         * 点击监听
         * @param point
         * @param x
         * @param y
         */
        void onPress(ILayerPoint point, float x, float y);

        /**
         * 长按监听
         * @param point
         * @param x
         * @param y
         */
        void onLongPress(ILayerPoint point, float x, float y);

        /**
         * 空操作
         * @param x
         * @param y
         */
        void onNop(float x, float y);
    }
}
