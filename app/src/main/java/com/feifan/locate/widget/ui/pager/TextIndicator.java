package com.feifan.locate.widget.ui.pager;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feifan.baselib.utils.LogUtils;

import java.lang.ref.WeakReference;

/**
 * 页面指示器
 * <p>
 *     显示文本
 * </p>
 *
 * Created by xuchunlei on 16/9/20.
 */
public class TextIndicator extends LinearLayout implements View.OnClickListener {

    // view
    private ViewPager mPager;
    private TextView mCurIndicator;
    private IndicatorOnPageChangeListener mPageChangeListener;

    // size & color
    private int mSpacing;
    private static final int COLOR_SELECTED = Color.BLUE;
    private static final int COLOR_UNSELECTED = Color.argb(0x88, 0x10, 0x10, 0x10);
    private static final int FONT_SIZE_SELECTED = 18;
    private static final int FONT_SIZE_UNSELECTED = 15;

    public TextIndicator(Context context) {
        this(context, null);
    }

    public TextIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.VERTICAL);
        mSpacing = dp2px(context, 5);
    }

    public void setupWithViewPager(ViewPager pager) {
        this.mPager = pager;
        if(mPageChangeListener == null) {
            mPageChangeListener = new IndicatorOnPageChangeListener(this);
        }
        mPager.addOnPageChangeListener(mPageChangeListener);

        PagerAdapter adapter = pager.getAdapter();
        if(adapter == null) {
            throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
        }else {
            setIndicatorsFromAdapter(adapter);
        }

    }

    private void setIndicatorsFromAdapter(PagerAdapter adapter) {
        this.removeAllViews();
        int i = 0;

        for(int count = adapter.getCount(); i < count; ++i) {
            TextView text = new TextView(getContext());
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(mSpacing, mSpacing, mSpacing, mSpacing);
            text.setLayoutParams(params);
            text.setPadding(mSpacing, mSpacing, mSpacing, mSpacing);
            text.setText(adapter.getPageTitle(i));
            text.setTextColor(COLOR_UNSELECTED);
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE_UNSELECTED);
            text.setTag(i);
            text.setOnClickListener(this);
            this.addView(text);
        }

        final int curItem = mPager.getCurrentItem();
        if (curItem != getSelectedPosition() && curItem < getChildCount()) {
            selectIndicator(getIndicatorAt(curItem));
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if(mCurIndicator != null) {
            unSelectIndicator();
        }
        if(v != mCurIndicator) {
            selectIndicator((TextView)v);
        }
    }

    private TextView getIndicatorAt(int position) {
        View v = getChildAt(position);
        return v != null ? (TextView)v : null;
    }

    private int getSelectedPosition() {
        if(mCurIndicator != null) {
            return (Integer)mCurIndicator.getTag();
        }
        return -1;
    }

    private void selectIndicator(TextView indicator) {
        int position = (Integer)indicator.getTag();
        indicator.setTextColor(COLOR_SELECTED);
        indicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE_SELECTED);
        mPager.setCurrentItem(position, true);
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

    /**
     * 页面变化监听接口
     */
    public static class IndicatorOnPageChangeListener implements ViewPager.OnPageChangeListener {

        private final WeakReference<TextIndicator> mIndicatorRef;

        public IndicatorOnPageChangeListener(TextIndicator indicator) {
            mIndicatorRef = new WeakReference<TextIndicator>(indicator);
        }

        /**
         * This method will be invoked when the current page is scrolled, either as part
         * of a programmatically initiated smooth scroll or a user initiated touch scroll.
         *
         * @param position             Position index of the first page currently being displayed.
         *                             Page position+1 will be visible if positionOffset is nonzero.
         * @param positionOffset       Value from [0, 1) indicating the offset from the page at position.
         * @param positionOffsetPixels Value in pixels indicating the offset from position.
         */
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        /**
         * This method will be invoked when a new page becomes selected. Animation is not
         * necessarily complete.
         *
         * @param position Position index of the new selected page.
         */
        @Override
        public void onPageSelected(int position) {
            final TextIndicator indicator = mIndicatorRef.get();
            if (indicator != null && indicator.getSelectedPosition() != position) {
                // Select the tab, only updating the indicator if we're not being dragged/settled
                // (since onPageScrolled will handle that).
//                final boolean update = mScrollState == SCROLL_STATE_IDLE
//                        || (mScrollState == SCROLL_STATE_SETTLING
//                        && mPreviousScrollState == SCROLL_STATE_IDLE);
//                indicator.selectTab(indicator.getTabAt(position), update);
                indicator.unSelectIndicator();
                indicator.selectIndicator(indicator.getIndicatorAt(position));
            }

        }

        /**
         * Called when the scroll state changes. Useful for discovering when the user
         * begins dragging, when the pager is automatically settling to the current page,
         * or when it is fully stopped/idle.
         *
         * @param state The new scroll state.
         * @see ViewPager#SCROLL_STATE_IDLE
         * @see ViewPager#SCROLL_STATE_DRAGGING
         * @see ViewPager#SCROLL_STATE_SETTLING
         */
        @Override
        public void onPageScrollStateChanged(int state) {
            LogUtils.e("onPageScrollStateChanged---->" + state);
        }
    }
}
