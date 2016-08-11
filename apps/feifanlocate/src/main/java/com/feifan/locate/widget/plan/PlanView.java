package com.feifan.locate.widget.plan;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.feifan.locate.R;

/**
 * 用于平面图操作的ImageView
 * <pre>
 *     载入平面图的图片后，可进行缩放、平移、标记等操作
 * </pre>
 * Created by xuchunlei on 16/8/10.
 */
public class PlanView extends ImageView {

    // 缩放
    private ScaleGestureDetector mScaleDetector;
    private float minScale = 1;       // 最小缩小倍数
    private float maxScale = 3;       // 最大放大倍数
    private float scale = 1;          // 当前缩放倍数
    private Matrix scaleMatrix = new Matrix();  // 用于缩放的矩阵

    // 尺寸
    private int viewWidth;
    private int viewHeight;
    private float matchViewWidth; // 实际显示平面图的View宽度
    private float matchViewHeight;// 实际显示平面图的View高度

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
        mMarkerLayer.setMark(bmpMark);

        // 让GestureDetector起作用
        setClickable(true);

        mScaleDetector = new ScaleGestureDetector(context, new PlanScaleListener());
        setScaleType(ScaleType.MATRIX);
        setImageMatrix(scaleMatrix);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // 没有图片或者图片无法显示
        Drawable drawable = getDrawable();
        if(drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
            setMeasuredDimension(0, 0);
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

        fitImageToView();

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

    // 调整图片适应View
    private void fitImageToView() {
        Drawable drawable = getDrawable();
        if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
            return;
        }

        if(scaleMatrix == null) {
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

        scaleMatrix.setScale(scale, scale);
        scaleMatrix.postTranslate(redundantXSpace / 2, redundantYSpace / 2);

        fixTrans();
        setImageMatrix(scaleMatrix);

    }

    // 进行边界检查，如果image小于view，则进行居中调整
    private void fixScaleTrans() {
        fixTrans();
        scaleMatrix.getValues(m);
        if (getImageWidth() < viewWidth) {
            m[Matrix.MTRANS_X] = (viewWidth - getImageWidth()) / 2;
        }

        if (getImageHeight() < viewHeight) {
            m[Matrix.MTRANS_Y] = (viewHeight - getImageHeight()) / 2;
        }
        scaleMatrix.setValues(m);
    }

    // 进行边界检查，scaleMatrix后的image位置超出view边界时，将image和view移动至坐标系重合
    private void fixTrans() {
        scaleMatrix.getValues(m);
        float transX = m[Matrix.MTRANS_X];
        float transY = m[Matrix.MTRANS_Y];

        float fixTransX = getFixTrans(transX, viewWidth, getImageWidth());
        float fixTransY = getFixTrans(transY, viewHeight, getImageHeight());

        if (fixTransX != 0 || fixTransY != 0) {
            scaleMatrix.postTranslate(fixTransX, fixTransY);
        }
    }

    // 获取修复后的平移距离，平移距离可以让image和view的坐标远点重合
    private float getFixTrans(float trans, float viewSize, float contentSize) {
        float minTrans, maxTrans;

        if (contentSize <= viewSize) { // 显示内容小于view尺寸
            minTrans = 0;
            maxTrans = viewSize - contentSize;
        } else {                       // 显示内容大于view尺寸
            minTrans = viewSize - contentSize;
            maxTrans = 0;
        }

        if (trans < minTrans)
            return -trans + minTrans;
        if (trans > maxTrans)
            return -trans + maxTrans;
        return 0;
    }

    private float getImageWidth() {
        return matchViewWidth * scale;
    }

    private float getImageHeight() {
        return matchViewHeight * scale;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 处理缩放手势
        mScaleDetector.onTouchEvent(event);
        // 处理标记层
        mMarkerLayer.onTouchEvent(event);

        // 使缩放生效
        setImageMatrix(scaleMatrix);

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制标记
        mMarkerLayer.draw(canvas);

    }

    /**
     * 缩放手势监听者
     */
    private class PlanScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleImage(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
            return true;
        }
    }

    /**
     * 缩放图片
     * @param deltaScale
     * @param focusX
     * @param focusY
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
        scaleMatrix.postScale((float) deltaScale, (float) deltaScale, focusX, focusY);

        fixScaleTrans();
    }
}
