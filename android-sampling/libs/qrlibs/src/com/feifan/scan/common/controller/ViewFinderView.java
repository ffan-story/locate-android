package com.feifan.scan.common.controller;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.wanda.qr.R;


public class ViewFinderView extends View implements IViewFinder {
    private static final String TAG = "ViewFinderView";

    private Rect mFramingRect;

    private static final int MIN_FRAME_WIDTH = 240;
    private static final int MIN_FRAME_HEIGHT = 240;

    private static final float LANDSCAPE_WIDTH_RATIO = 5f / 8;
    private static final float LANDSCAPE_HEIGHT_RATIO = 5f / 8;
    private static final int LANDSCAPE_MAX_FRAME_WIDTH = (int) (1920 * LANDSCAPE_WIDTH_RATIO); // = 5/8 * 1920
    private static final int LANDSCAPE_MAX_FRAME_HEIGHT = (int) (1080 * LANDSCAPE_HEIGHT_RATIO); // = 5/8 * 1080

    private static final float PORTRAIT_WIDTH_RATIO = 5f / 8;
    private static final float PORTRAIT_HEIGHT_RATIO = 3f / 8;
    private static final int PORTRAIT_MAX_FRAME_WIDTH = (int) (1080 * PORTRAIT_WIDTH_RATIO); // = 7/8 * 1080
    private static final int PORTRAIT_MAX_FRAME_HEIGHT = (int) (1920 * PORTRAIT_HEIGHT_RATIO); // = 3/8 * 1920

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private int scannerAlpha;
    private static final int POINT_SIZE = 10;
    private static final long ANIMATION_DELAY = 80l;

    private final int mDefaultLaserColor = getResources().getColor(R.color.viewfinder_laser);
    private final int mDefaultMaskColor = getResources().getColor(R.color.viewfinder_mask);
    private final int mDefaultBorderColor = getResources().getColor(R.color.viewfinder_border);
    private final int mDefaultBorderStrokeWidth = getResources().getInteger(R.integer.viewfinder_border_width);
    private final int mDefaultBorderLineLength = getResources().getInteger(R.integer.viewfinder_border_length);
    protected Paint mLaserPaint;
    protected Paint mFinderMaskPaint;
    protected Paint mBorderPaint;
    protected int mBorderLineLength;
    private Bitmap mScanBitMap;

    public ViewFinderView(Context context) {
        super(context);
        init();
    }

    public ViewFinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        //set up laser paint
        mLaserPaint = new Paint();
        mLaserPaint.setColor(mDefaultLaserColor);
        mLaserPaint.setStyle(Paint.Style.FILL);

        //finder mask paint
        mFinderMaskPaint = new Paint();
        mFinderMaskPaint.setColor(mDefaultMaskColor);

        //border paint
        mBorderPaint = new Paint();
        mBorderPaint.setColor(mDefaultBorderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mDefaultBorderStrokeWidth);

        mBorderLineLength = mDefaultBorderLineLength;
    }

    public void setLaserColor(int laserColor) {
        mLaserPaint.setColor(laserColor);
    }

    public void setMaskColor(int maskColor) {
        mFinderMaskPaint.setColor(maskColor);
    }

    public void setBorderColor(int borderColor) {
        mBorderPaint.setColor(borderColor);
    }

    public void setBorderStrokeWidth(int borderStrokeWidth) {
        mBorderPaint.setStrokeWidth(borderStrokeWidth);
    }

    public void setBorderLineLength(int borderLineLength) {
        mBorderLineLength = borderLineLength;
    }

    public void setupViewFinder() {
        updateFramingRect();
        invalidate();
    }

    public Rect getFramingRect() {
        return mFramingRect;
    }

    /**
     * @param rawResult
     * @param barcode
     */
    @Override
    public void performZXingSucess(Result rawResult, Bitmap barcode) {
            drawResultPoints(rawResult, barcode);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mFramingRect == null) {
            return;
        }

        drawViewFinderMask(canvas);
        drawViewFinderBorder(canvas);
        drawLaser(canvas);
        if (mScanBitMap != null) {
            canvas.drawBitmap(mScanBitMap,mFramingRect.left,mFramingRect.top,null);
        }
    }

    public void drawViewFinderMask(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        canvas.drawRect(0, 0, width, mFramingRect.top, mFinderMaskPaint);
        canvas.drawRect(0, mFramingRect.top, mFramingRect.left, mFramingRect.bottom + 1, mFinderMaskPaint);
        canvas.drawRect(mFramingRect.right + 1, mFramingRect.top, width, mFramingRect.bottom + 1, mFinderMaskPaint);
        canvas.drawRect(0, mFramingRect.bottom + 1, width, height, mFinderMaskPaint);
    }

    public void drawViewFinderBorder(Canvas canvas) {
        canvas.drawLine(mFramingRect.left - 1, mFramingRect.top - 1, mFramingRect.left - 1, mFramingRect.top - 1 + mBorderLineLength, mBorderPaint);
        canvas.drawLine(mFramingRect.left - 1, mFramingRect.top - 1, mFramingRect.left - 1 + mBorderLineLength, mFramingRect.top - 1, mBorderPaint);

        canvas.drawLine(mFramingRect.left - 1, mFramingRect.bottom + 1, mFramingRect.left - 1, mFramingRect.bottom + 1 - mBorderLineLength, mBorderPaint);
        canvas.drawLine(mFramingRect.left - 1, mFramingRect.bottom + 1, mFramingRect.left - 1 + mBorderLineLength, mFramingRect.bottom + 1, mBorderPaint);

        canvas.drawLine(mFramingRect.right + 1, mFramingRect.top - 1, mFramingRect.right + 1, mFramingRect.top - 1 + mBorderLineLength, mBorderPaint);
        canvas.drawLine(mFramingRect.right + 1, mFramingRect.top - 1, mFramingRect.right + 1 - mBorderLineLength, mFramingRect.top - 1, mBorderPaint);

        canvas.drawLine(mFramingRect.right + 1, mFramingRect.bottom + 1, mFramingRect.right + 1, mFramingRect.bottom + 1 - mBorderLineLength, mBorderPaint);
        canvas.drawLine(mFramingRect.right + 1, mFramingRect.bottom + 1, mFramingRect.right + 1 - mBorderLineLength, mFramingRect.bottom + 1, mBorderPaint);
    }

    public void drawLaser(Canvas canvas) {
        // Draw a red "laser scanner" line through the middle to show decoding is active
        mLaserPaint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
        scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
        int middle = mFramingRect.height() / 2 + mFramingRect.top;
        canvas.drawRect(mFramingRect.left + 2, middle - 1, mFramingRect.right - 1, middle + 2, mLaserPaint);

        postInvalidateDelayed(ANIMATION_DELAY,
                mFramingRect.left - POINT_SIZE,
                mFramingRect.top - POINT_SIZE,
                mFramingRect.right + POINT_SIZE,
                mFramingRect.bottom + POINT_SIZE);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        updateFramingRect();
    }

    public synchronized void updateFramingRect() {
        Point viewResolution = new Point(getWidth(), getHeight());
        int width;
        int height;
        int orientation = DisplayUtils.getScreenOrientation(getContext());

        if (orientation != Configuration.ORIENTATION_PORTRAIT) {
            width = findDesiredDimensionInRange(LANDSCAPE_WIDTH_RATIO, viewResolution.x, MIN_FRAME_WIDTH, LANDSCAPE_MAX_FRAME_WIDTH);
            height = findDesiredDimensionInRange(LANDSCAPE_HEIGHT_RATIO, viewResolution.y, MIN_FRAME_HEIGHT, LANDSCAPE_MAX_FRAME_HEIGHT);
        } else {
            width = findDesiredDimensionInRange(PORTRAIT_WIDTH_RATIO, viewResolution.x, MIN_FRAME_WIDTH, PORTRAIT_MAX_FRAME_WIDTH);
            height = width;
            //自定义扫描框的大小
//                    findDesiredDimensionInRange(PORTRAIT_HEIGHT_RATIO, viewResolution.y, MIN_FRAME_HEIGHT, PORTRAIT_MAX_FRAME_HEIGHT);
        }

        int leftOffset = (viewResolution.x - width) / 2;
        int topOffset = (viewResolution.y - height) / 2;
        mFramingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
    }

    private static int findDesiredDimensionInRange(float ratio, int resolution, int hardMin, int hardMax) {
        int dim = (int) (ratio * resolution);
        if (dim < hardMin) {
            return hardMin;
        }
        if (dim > hardMax) {
            return hardMax;
        }
        return dim;
    }

    /**
     * Superimpose a line for 1D or dots for 2D to highlight the key features of
     * the barcode.
     *
     * @param barcode   A bitmap of the captured image.
     * @param rawResult The decoded results which contains the points to draw.
     */
    private void drawResultPoints(Result rawResult, Bitmap barcode) {
        if(barcode == null || rawResult == null){
            return;
        }
        ResultPoint[] points = rawResult.getResultPoints();
        if (points != null && points.length > 0) {
            Canvas canvas = new Canvas(barcode);
            Paint paint = new Paint();
            paint.setColor(getContext().getResources().getColor(
                    R.color.result_image_border));
            paint.setStrokeWidth(3.0f);
            paint.setStyle(Paint.Style.STROKE);
            Rect border = new Rect(2, 2, barcode.getWidth() - 2,
                    barcode.getHeight() - 2);
            canvas.drawRect(border, paint);

            paint.setColor(getContext().getResources().getColor(
                    R.color.result_points));
            if (points.length == 2) {
                paint.setStrokeWidth(4.0f);
                drawLine(canvas, paint, points[0], points[1]);
            } else if (points.length == 4
                    && (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A || rawResult
                    .getBarcodeFormat() == BarcodeFormat.EAN_13)) {
                // Hacky special case -- draw two lines, for the barcode and
                // metadata
                drawLine(canvas, paint, points[0], points[1]);
                drawLine(canvas, paint, points[2], points[3]);
            } else {
                paint.setStrokeWidth(10.0f);
                for (ResultPoint point : points) {
                    canvas.drawPoint(point.getX(), point.getY(), paint);
                }
            }
            mScanBitMap = scaleImage(barcode,mFramingRect.width(),mFramingRect.height());
            postInvalidateDelayed(ANIMATION_DELAY,
                    mFramingRect.left - POINT_SIZE,
                    mFramingRect.top - POINT_SIZE,
                    mFramingRect.right + POINT_SIZE,
                    mFramingRect.bottom + POINT_SIZE);
        }
    }

    /**
     * 按新的宽高缩放图片
     *
     * @param bm
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap scaleImage(Bitmap bm, int newWidth, int newHeight) {
        if (bm == null) {
            return null;
        }
        try {
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
                    true);
            if (bm != null & !bm.isRecycled()) {
                bm.recycle();
                bm = null;
            }
            return newbm;
        }catch (Exception e){
            e.printStackTrace();
        }catch(OutOfMemoryError e){

        }
        return  null;
    }

    private void drawLine(Canvas canvas, Paint paint, ResultPoint a,
                          ResultPoint b) {
        canvas.drawLine(a.getX(), a.getY(), b.getX(), b.getY(), paint);
    }
}
