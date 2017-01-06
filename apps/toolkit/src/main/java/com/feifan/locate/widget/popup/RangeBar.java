package com.feifan.locate.widget.popup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * 弹出窗口控件-选取范围值
 * Created by bianying on 2017/1/3.
 */

public class RangeBar extends View {

    // 默认宽度
    private int mDefaultWidth = 500;
    // 默认高度
    private int mDefaultHeight = 150;

    public RangeBar(Context context) {
        super(context);
    }

    public RangeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RangeBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;

        // 获取测量模式和尺寸
//        final int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
//        final int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);
//        final int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
//        final int measureHeight = MeasureSpec.getSize(heightMeasureSpec);

        // 宽度尽可能大
        width = getDefaultSize(mDefaultWidth, widthMeasureSpec);

        // 高度尽可能小
        height = Math.min(mDefaultHeight, getDefaultSize(mDefaultHeight, heightMeasureSpec));

//        if (measureWidthMode == MeasureSpec.AT_MOST) {
//            width = measureWidth;
//        } else if (measureWidthMode == MeasureSpec.EXACTLY) {
//            width = measureWidth;
//        } else {
//            width = mDefaultWidth;
//        }

//        if (measureHeightMode == MeasureSpec.AT_MOST) {
//            height = Math.min(mDefaultHeight, measureHeight);
//        } else if (measureHeightMode == MeasureSpec.EXACTLY) {
//            height = measureHeight;
//        } else {
//            height = mDefaultHeight;
//        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//
//        final Context ctx = getContext();
//
//        // This is the initial point at which we know the size of the View.
//
//        // Create the two thumb objects and position line in view
//        float density = getResources().getDisplayMetrics().density;
//        float expandedPinRadius = mExpandedPinRadius / density;
//
//        final float yPos = h - mBarPaddingBottom;
//        if (mIsRangeBar) {
//            mLeftThumb = new PinView(ctx);
//            mLeftThumb.setFormatter(mFormatter);
//            mLeftThumb.init(ctx, yPos, expandedPinRadius, mPinColor, mTextColor, mCircleSize,
//                    mCircleColor, mMinPinFont, mMaxPinFont, mArePinsTemporary);
//        }
//        mRightThumb = new PinView(ctx);
//        mRightThumb.setFormatter(mFormatter);
//        mRightThumb.init(ctx, yPos, expandedPinRadius, mPinColor, mTextColor, mCircleSize,
//                mCircleColor, mMinPinFont, mMaxPinFont, mArePinsTemporary);
//
//        // Create the underlying bar.
//        final float marginLeft = Math.max(mExpandedPinRadius, mCircleSize);
//
//        final float barLength = w - (2 * marginLeft);
//        mBar = new Bar(ctx, marginLeft, yPos, barLength, mTickCount, mTickHeightDP, mTickColor,
//                mBarWeight, mBarColor);
//
//        // Initialize thumbs to the desired indices
//        if (mIsRangeBar) {
//            mLeftThumb.setX(marginLeft + (mLeftIndex / (float) (mTickCount - 1)) * barLength);
//            mLeftThumb.setXValue(getPinValue(mLeftIndex));
//        }
//        mRightThumb.setX(marginLeft + (mRightIndex / (float) (mTickCount - 1)) * barLength);
//        mRightThumb.setXValue(getPinValue(mRightIndex));
//
//        // Set the thumb indices.
//        final int newLeftIndex = mIsRangeBar ? mBar.getNearestTickIndex(mLeftThumb) : 0;
//        final int newRightIndex = mBar.getNearestTickIndex(mRightThumb);
//
//        // Call the listener.
//        if (newLeftIndex != mLeftIndex || newRightIndex != mRightIndex) {
//            if (mListener != null) {
//                mListener.onRangeChangeListener(this, mLeftIndex, mRightIndex,
//                        getPinValue(mLeftIndex),
//                        getPinValue(mRightIndex));
//            }
//        }
//
//        // Create the line connecting the two thumbs.
//        mConnectingLine = new ConnectingLine(ctx, yPos, mConnectingLineWeight,
//                mConnectingLineColor);
    }
}
