package com.feifan.locate.widget.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by xuchunlei on 2016/10/28.
 */

public class TextIndicator extends LinearLayout implements View.OnClickListener {

    // view
    private TextView mCurIndicator;

    // size & color
    private int mSpacing;
    private static final int COLOR_SELECTED = Color.BLUE;
    private static final int COLOR_UNSELECTED = Color.argb(0x88, 0x10, 0x10, 0x10);
    private static final int FONT_SIZE_SELECTED = 18;
    private static final int FONT_SIZE_UNSELECTED = 15;

    private TextIndicatorModel mCompareModel = new TextIndicatorModel();

    public TextIndicator(Context context) {
        this(context, null);
    }

    public TextIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(Color.argb(0x55, 0x55, 0x55, 0x55));
        mSpacing = dp2px(context, 5);
    }

    public void setData(List<TextIndicatorModel> data) {
        removeAllViews();
        for(TextIndicatorModel item : data) {
            TextView text = new TextView(getContext());
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(mSpacing, mSpacing, mSpacing, mSpacing);
            text.setLayoutParams(params);
            text.setPadding(mSpacing, mSpacing, mSpacing, mSpacing);
            text.setId(item.id);
            text.setText(item.text);
            text.setTag(item);
            text.setTextColor(COLOR_UNSELECTED);
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE_UNSELECTED);
            text.setOnClickListener(this);
            this.addView(text);
        }
    }

    public void setCurrent(int index) {
        mCompareModel.id = index;
        View v = findViewWithTag(mCompareModel);
        if(v != null) {
            v.performClick();
        }
    }

    @Override
    public void onClick(View v) {
        if(mCurIndicator != null) {
            unSelectIndicator();
        }
        if(v != mCurIndicator) {
            selectIndicator((TextView)v);
        }
    }

    public static class TextIndicatorModel {
        public int id;
        public String text;

        public TextIndicatorModel() {

        }

        public TextIndicatorModel(int id, String text) {
            this.id = id;
            this.text = text;
        }

        @Override
        public boolean equals(Object o) {
            if(!(o instanceof TextIndicatorModel)) {
                return false;
            }
            TextIndicatorModel model = (TextIndicatorModel)o;
            return id == model.id;
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + id;
            return result;
        }
    }

    private void selectIndicator(TextView indicator) {
        indicator.setTextColor(COLOR_SELECTED);
        indicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE_SELECTED);
        mCurIndicator = indicator;
    }

    private void unSelectIndicator() {
        mCurIndicator.setTextColor(COLOR_UNSELECTED);
        mCurIndicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE_UNSELECTED);
    }

    private int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }
}
