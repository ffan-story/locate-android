package com.libs.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.wanda.logger.toolbox.SystemUtil;


public class ProgressBarCircular extends RelativeLayout {

	final static String ANDROIDXML = "http://schemas.android.com/apk/res/android";

	private final String DEFAULT_COLOR = "#1E88E5";
	private int mBackgroundColor = Color.parseColor("#1E88E5");

	private final int CIRCLE_WIDTH = 2;
    private float radius1 = 0;
    private float radius2 = 0;
    private int cont = 0;
    // 通过这个参数配置loading的缩放的速度
    private final int INCREASE_SIZE = 3;
    private boolean firstAnimationOver = false;

    private int mRadius;

	public ProgressBarCircular(Context context, AttributeSet attrs) {
		super(context, attrs);
		setAttributes(attrs);
	}

	// Set atributtes of XML to View
	protected void setAttributes(AttributeSet attrs) {
		setMinimumHeight(SystemUtil.dip2px(getContext(), 32));
		setMinimumWidth(SystemUtil.dip2px(getContext(), 32));
		int bacgroundColor = attrs.getAttributeResourceValue(ANDROIDXML, "background", -1);
		if (bacgroundColor != -1) {
			setBackgroundColor(getResources().getColor(bacgroundColor));
		} else {
			String background = attrs.getAttributeValue(ANDROIDXML, "background");
			if (background != null)
				setBackgroundColor(Color.parseColor(background));
			else
				setBackgroundColor(Color.parseColor(DEFAULT_COLOR));
		}
		setMinimumHeight(SystemUtil.dip2px(getContext(), 3));

	}

	/**
	 * Make a dark color to ripple effect
	 * 
	 * @return
	 */
	protected int makePressColor() {
		int r = (this.mBackgroundColor >> 16) & 0xFF;
		int g = (this.mBackgroundColor >> 8) & 0xFF;
		int b = (this.mBackgroundColor >> 0) & 0xFF;
		return Color.argb(128, r, g, b);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
        mRadius = getWidth()/2 - CIRCLE_WIDTH;
//		if (firstAnimationOver == false)
//			drawFirstAnimation(canvas);
//		if (cont > 0)
		drawSecondAnimation(canvas);
		invalidate();

	}

	/**
	 * Draw first animation of view 放大缩小的效果
	 * 
	 * @param canvas
	 */
	private void drawFirstAnimation(Canvas canvas) {
		// Log.e("The radius1 is: ", radius1+"");
		if (radius1 < mRadius) {
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(makePressColor());
			radius1 = (radius1 >= mRadius) ? (float) mRadius: radius1 + INCREASE_SIZE;
			canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, paint);
		} else {
			Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas temp = new Canvas(bitmap);
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(makePressColor());
			temp.drawCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2, paint);
			Paint transparentPaint = new Paint();
			transparentPaint.setAntiAlias(true);
			transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
			transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			if (cont >= 50) {
				radius2 = (radius2 >= mRadius) ? (float) mRadius : radius2 + INCREASE_SIZE;
			} else {
				radius2 = (radius2 >= mRadius) ? (float) mRadius : radius2 + INCREASE_SIZE;
			}
			canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius2, transparentPaint);
			canvas.drawBitmap(bitmap, 0, 0, new Paint());
			if (radius2 >= mRadius)
				cont ++ ;
			if (radius2 >= mRadius)
				firstAnimationOver = true;
			bitmap.recycle();
		}
	}

	int arcD = 1;
	int arcO = 0;
	float rotateAngle = 0;
	int limite = 0;

	/**
	 * Draw second animation of view 循环旋转地效果  以290为分界点
	 * 
	 * @param canvas
	 */
	private void drawSecondAnimation(Canvas canvas) {
		if (arcO == limite)
			arcD += 6;

		if (arcD >= 290 || arcO > limite) {
			arcO += 6;
			arcD -= 6;
		}
		if (arcO > limite + 290) {
			arcO = arcO % 360;
			limite = arcO;
			arcD = 1;
		}
		rotateAngle += 6;
		canvas.rotate(rotateAngle, getWidth()/2,getHeight()/2);
        canvas.save();
//		Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
//		Canvas temp = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(CIRCLE_WIDTH * 2);
		paint.setColor(mBackgroundColor);
		// temp.drawARGB(0, 0, 0, 255);
        canvas.drawArc(new RectF(CIRCLE_WIDTH, CIRCLE_WIDTH, mRadius * 2, mRadius * 2), arcO, arcD, false, paint);
		Paint transparentPaint = new Paint();
		transparentPaint.setAntiAlias(true);
		transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
		transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
//        canvas.drawCircle(getWidth() / 2, getHeight() / 2, (getWidth() / 2) - SystemUtil.dip2px(getContext(), CIRCLE_WIDTH), transparentPaint);
//		canvas.drawBitmap(bitmap, 0, 0, new Paint());
        canvas.restore();
	}

	// Set color of background
	public void setBackgroundColor(int color) {
		super.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		this.mBackgroundColor = color;
	}

}
