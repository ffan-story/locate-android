package com.feifan.locate.widget.settingwork;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.R;

/**
 * Created by bianying on 2016/10/6.
 */

public class SettingLayout extends LinearLayout {

    private Drawable mDivider;

    public SettingLayout(Context context) {
        this(context, null, 0);
    }

    public SettingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
//        mDivider = new ColorDrawable(ContextCompat.getColor(context, R.color.setting_item_divider_color));
        mDivider = createDivider(ContextCompat.getColor(context, R.color.setting_item_divider_color));
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, paint);

//        final int count = getChildCount();
//        if(count == 0) {
//            return;
//        }
//        for(int i = 0;i < count;i++) {
//            View child = getChildAt(i);
//            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
//            final int top = child.getTop() - lp.topMargin - mDividerHeight;
//            drawDivider(canvas, top);
//        }
    }

    private Drawable createDivider(int color) {
        int bitmap_size = 64;
        Bitmap bitmap = Bitmap.createBitmap(bitmap_size, bitmap_size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(new Rect(0, 0, bitmap_size, bitmap_size), paint);
        return new BitmapDrawable(getResources(), bitmap);
    }

    private int mDividerHeight = 2;
    private int mDividerPadding = 0;
    private void drawDivider(Canvas canvas, int top) {
        mDivider.setBounds(getPaddingLeft() + mDividerPadding, top,
                getWidth() - getPaddingRight() - mDividerPadding, top + mDividerHeight);

        mDivider.draw(canvas);
    }
}
