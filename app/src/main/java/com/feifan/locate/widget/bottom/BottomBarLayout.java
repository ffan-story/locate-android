package com.feifan.locate.widget.bottom;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.XmlRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feifan.locate.R;

import java.util.ArrayList;

/**
 * 底部栏
 * <p>
 *     实现google底部导航设计规范
 *     https://material.google.com/components/bottom-navigation.html#
 * </p>
 *
 * Created by xuchunlei on 16/8/2.
 */
public class BottomBarLayout extends RelativeLayout implements View.OnClickListener {

    private static final String TAG_BOTTOM_BAR_TAB_INACTIVE = "BOTTOM_BAR_TAB_INACTIVE";
    private static final String TAG_BOTTOM_BAR_TAB_ACTIVE = "BOTTOM_BAR_TAB_ACTIVE";

    // 相关UI对象
    private ViewGroup mTabsContainer;

    // 相关资源
    private Integer mPrimaryColor;
    private Integer mInactiveColor;
    private int mFragmentContainer;

    // 相关数据
    private ArrayList<BottomItem> mTabs;

    // 相关工具
    private FragmentManager mFragmentManager;

    private int mScreenWidth;
    private int mMaxItemWidth;
    private int mCurrentTabPosition;

    public BottomBarLayout(Context context) {
        this(context, null);
    }

    public BottomBarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScreenWidth = BottomBarUtils.getScreenWidth(context);
        mMaxItemWidth = BottomBarUtils.dpToPixel(context, 168); // 最大宽度168dp,来自google的spec
        mCurrentTabPosition = -1;
        mPrimaryColor = BottomBarUtils.getColor(getContext(), R.attr.colorPrimary);
        mInactiveColor = ContextCompat.getColor(getContext(), R.color.colorTabItemInactive);
        mFragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
    }


    /**
     * 实例化BottomBar，绑定到活动
     * @param activity
     * @return
     */
    public static BottomBarLayout attach(Activity activity) {
        BottomBarLayout bottomBar = new BottomBarLayout(activity);

        ViewGroup contentView = (ViewGroup)activity.findViewById(android.R.id.content);
        View oldLayout = contentView.getChildAt(0);
        contentView.removeView(oldLayout);

        contentView.addView(bottomBar, 0);


        return bottomBar;
    }

    /**
     * 设置显示在bottom的menu项
     * @param itemsRes
     */
    public void setTabs(@XmlRes int itemsRes) {
        clearItems();
        mTabs = BottomBarUtils.loadTabs(getContext(), itemsRes);
        updateTabs(mTabs);
        setDefaultTabPosition(0);
    }

    @Override
    public void onClick(View v) {
        if(v.getTag().equals(TAG_BOTTOM_BAR_TAB_INACTIVE)) {
            View exCurrent = findViewWithTag(TAG_BOTTOM_BAR_TAB_ACTIVE);
            unselectTab(exCurrent);
            selectTab(v);
        }
        updateSelectedTab(findItemPosition(v));
    }

    /**
     * 设置默认Tab位置
     * @param position
     */
    public void setDefaultTabPosition(int position) {
        if(mTabs == null || mTabs.size() == 0 || position > mTabs.size() - 1 || position < 0) {
            throw new IndexOutOfBoundsException("Can't set default tab at position " + position +
            ". Check whether the position is valid or Tabs were initialized normally.");
        }

        View oldTab = mTabsContainer.findViewWithTag(TAG_BOTTOM_BAR_TAB_ACTIVE);
        View newTab = mTabsContainer.getChildAt(position);

        if(oldTab != null) {
            unselectTab(oldTab);
        }

        selectTab(newTab);

        updateSelectedTab(position);
    }

    private void clearItems(){
        if(mTabs != null) {
            mTabs = null;
        }
    }

    private void updateTabs(ArrayList<BottomItem> tabs) {

        if(mTabsContainer == null) {
            initViews();
        }

        // 实例化Tab项的一些参数
        int computedWidth = Math.min(BottomBarUtils.dpToPixel(getContext(), mScreenWidth / tabs.size()), mMaxItemWidth);
        int height = Math.round(getContext().getResources().getDimension(R.dimen.bottombar_height));
        int index = 0;

        for(BottomItem item : tabs) {
            View tab = View.inflate(getContext(), R.layout.bottombar_tab_item, null);
            AppCompatImageView icon = (AppCompatImageView)tab.findViewById(R.id.bottombar_tab_item_icon);
            icon.setImageResource(item.iconRes);
            TextView title = (TextView)tab.findViewById(R.id.bottombar_tab_item_title);
            title.setText(item.titleRes);

            // 更新状态
            if(index == mCurrentTabPosition) {
                selectTab(tab);
            } else {
                unselectTab(tab);
            }

            // 添加到布局
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(computedWidth, height);
            tab.setLayoutParams(params);
            mTabsContainer.addView(tab);
            tab.setOnClickListener(this);

            index++;
        }

    }

    private void initViews() {
        View rootV = inflate(getContext(), R.layout.bottombar_item_container, this);
        mTabsContainer = (ViewGroup) rootV.findViewById(R.id.bottombar_tab_container);
        mFragmentContainer = R.id.bottombar_user_content_container;
    }

    // 更新选中标签项的状态
    private void selectTab(View tab) {
        tab.setTag(TAG_BOTTOM_BAR_TAB_ACTIVE);
        AppCompatImageView icon = (AppCompatImageView)tab.findViewById(R.id.bottombar_tab_item_icon);
        TextView title = (TextView)tab.findViewById(R.id.bottombar_tab_item_title);
        icon.setColorFilter(mPrimaryColor);
        title.setTextColor(mPrimaryColor);
    }

    // 更新未选中标签项的状态
    private void unselectTab(View tab) {
        tab.setTag(TAG_BOTTOM_BAR_TAB_INACTIVE);
        AppCompatImageView icon = (AppCompatImageView)tab.findViewById(R.id.bottombar_tab_item_icon);
        TextView title = (TextView)tab.findViewById(R.id.bottombar_tab_item_title);
        icon.setColorFilter(mInactiveColor);
        title.setTextColor(mInactiveColor);
    }

    // 更新选中标签项的关联界面
    private void updateSelectedTab(int position) {

        if(position != mCurrentTabPosition) {    // 当前选中的tab发生变化
            mCurrentTabPosition = position;
            BottomItem item = mTabs.get(position);
            if(item instanceof BottomFragmentItem) { // 绑定Fragment的Tab项
                mFragmentManager.beginTransaction()
                        .replace(mFragmentContainer, ((BottomFragmentItem)item).getFragment(getContext()))
                        .commitAllowingStateLoss();
            }else {

            }
        }
    }

    // 获取tab项的位置
    private int findItemPosition(View viewToFind) {
        int position = 0;

        for (int i = 0; i < mTabsContainer.getChildCount(); i++) {
            View candidate = mTabsContainer.getChildAt(i);

            if (candidate.equals(viewToFind)) {
                position = i;
                break;
            }
        }

        return position;
    }
}
