package com.feifan.locate.widget.plan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.feifan.locate.R;
import com.feifan.locate.utils.LogUtils;

/**
 * 用于平面图操作的ImageView
 * <pre>
 *     载入平面图的图片后，可进行缩放、平移、标记等操作
 * </pre>
 * Created by xuchunlei on 16/8/10.
 */
public class PlanView extends ImageView {

    private static final String TAG = "PlanView";

    // 缩放
    private ScaleGestureDetector mScaleDetector;
    private float minScale = 1;       // 最小缩小倍数
    private float maxScale = 3;       // 最大放大倍数
    private float scale = 1;          // 当前缩放倍数

    // 点击
    private GestureDetector mGestureDetector;

    // 平移
    private PointF lastTouch = new PointF();

    // 图像变换
    private Matrix matrix = new Matrix();  // 用于图形变换的矩阵
    private float[] fixTransXY = new float[2]; // 缓存将img平移到view内的偏移量
    private float[] fixCenterXY = new float[2];  // 缓存缩放后将img平移到view中间位置的偏移量
    private float[] imgOriginXY = new float[2]; // img坐标系原点

    // 尺寸
    private int viewWidth;
    private int viewHeight;
    private float matchViewWidth; // 实际显示平面图的初始View宽度
    private float matchViewHeight;// 实际显示平面图的初始View高度

    // 操作状态
    private enum State {
        NONE, // 不进行任何操作时的状态
        ZOOM, // 缩放状态
        DRAG, // 拖拽状态
    }
    private State mState;

    // 用于获取matrix的参数
    private float[] m = new float[9];

    // 地图标记
    private MarkerLayer mMarkerLayer;

    public PlanView(Context context) {
        this(context, null);
    }

    public PlanView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 初始化
        mMarkerLayer = new MarkerLayer();
        Bitmap bmpMark = BitmapFactory.decodeResource(context.getResources(), R.mipmap.plan_mark);
        mMarkerLayer.setMarkIcon(bmpMark);

        // 让GestureDetector起作用
        setClickable(true);

        mScaleDetector = new ScaleGestureDetector(context, new PlanScaleListener());
        mGestureDetector = new GestureDetector(context, new PlanGestureListener());

        setScaleType(ScaleType.MATRIX);
        setImageMatrix(matrix);
        setState(State.NONE);
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

        // 初始化img坐标
        imgOriginXY[0] = (viewWidth - matchViewWidth) / 2;
        imgOriginXY[1] = (viewHeight - matchViewHeight) / 2;

        LogUtils.e("imgOriginXY-->" + imgOriginXY[0] + "," + imgOriginXY[1]);
    }

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

    /**
     * 锁定平面图
     *
     */
    public void lock() {
        mMarkerLayer.lockAllPoints();

        // 还原平面图原来的大小和位置，并禁止任何操作

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
        if(fixTrans(fixTransXY)) {
            mMarkerLayer.onMoveEvent(fixTransXY[0], fixTransXY[1], scale);
            computeMoveImageOrigin(fixTransXY[0], fixTransXY[1]);
        }

        // img进行居中平移，虽在缩放之前，但移动的是缩放之后的距离
        matrix.getValues(m);

        // 标记层偏移处理
        fixCenterXY[0] = getFixCenterTrans(m[Matrix.MTRANS_X], viewWidth, getImageWidth());
        fixCenterXY[1] = getFixCenterTrans(m[Matrix.MTRANS_Y], viewHeight, getImageHeight());
        if(fixCenterXY[0] != 0 || fixCenterXY[1] != 0) {
            mMarkerLayer.onMoveEvent(fixCenterXY[0], fixCenterXY[1], scale);
            computeMoveImageOrigin(fixCenterXY[0], fixCenterXY[1]);
        }

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
        // 处理缩放手势
        mScaleDetector.onTouchEvent(event);
        // 处理手势
        mGestureDetector.onTouchEvent(event);

        // 平移图片
        if(mState == State.NONE || mState == State.DRAG) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:          // 拖拽开始
                    lastTouch.set(event.getX(), event.getY());
                    setState(State.DRAG);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(mState == State.DRAG) {
                        float deltaX = event.getX() - lastTouch.x;
                        float deltaY = event.getY() - lastTouch.y;

                        float fixTranX = mMarkerLayer.hasUnLockedPoints() ? deltaX : getFixDragTrans(deltaX, viewWidth, getImageWidth());
                        float fixTranY = mMarkerLayer.hasUnLockedPoints() ? deltaY : getFixDragTrans(deltaY, viewHeight, getImageHeight());
                        moveALl(fixTranX, fixTranY, scale);

                        if(!mMarkerLayer.hasUnLockedPoints()) {    // 不存在未锁定的点，限制img在view内
                            if(fixTrans(fixTransXY)) {
                                mMarkerLayer.onMoveEvent(fixTransXY[0], fixTransXY[1], scale);
                            }
                        }
                        // TODO 计算img原点
                        computeMoveImageOrigin(fixTranX + fixTransXY[0], fixTranY + fixTransXY[1]);

                        lastTouch.set(event.getX(), event.getY());
                    }
                    break;
                case MotionEvent.ACTION_UP:            // 拖拽结束
                case MotionEvent.ACTION_POINTER_UP:
                    setState(State.NONE);
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

        // 绘制标记
        mMarkerLayer.draw(canvas);
    }

    // 设置操作状态
    private void setState(State state) {
        this.mState = state;
    }

    // 清除数据
    private void clearXY(float[] xy) {
        if(xy.length == 2) {
            xy[0] = 0;
            xy[1] = 0;
        }
    }

    // 移动所有元素
    private void moveALl(float transX, float transY, float scale) {
        matrix.postTranslate(transX, transY);
        // 标记层平移
        mMarkerLayer.onMoveEvent(transX, transY, scale);
    }

    // 计算img坐标系原点坐标
    private void computeScaleImageOrigin(float scale, float focusX, float focusY) {
        imgOriginXY[0] = (imgOriginXY[0] - focusX) * scale + focusX;
        imgOriginXY[1] = (imgOriginXY[1] - focusY) * scale + focusY;
        LogUtils.e("imgOriginXY-->" + imgOriginXY[0] + "," + imgOriginXY[1]);
    }

    private void computeMoveImageOrigin(float transX, float transY) {
        imgOriginXY[0] = imgOriginXY[0] + transX;
        imgOriginXY[1] = imgOriginXY[1] + transY;
        LogUtils.e("imgOriginXY-->" + imgOriginXY[0] + "," + imgOriginXY[1]);
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
            mMarkerLayer.addMark(e.getX(), e.getY(), imgOriginXY, scale);
            invalidate();
            return super.onSingleTapConfirmed(e);
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
        // 标记层
        mMarkerLayer.onScaleEvent((float) deltaScale, focusX, focusY);
        // 更新img原点
        computeScaleImageOrigin((float) deltaScale, focusX, focusY);

        fixScaleTrans();
    }
}
