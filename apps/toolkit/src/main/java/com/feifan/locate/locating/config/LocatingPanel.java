package com.feifan.locate.locating.config;

import android.content.Context;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.feifan.locate.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuchunlei on 2017/1/3.
 */

public class LocatingPanel extends RelativeLayout {

    @IdRes
    private static final int ID_TABS = 1;

    private FrameLayout.LayoutParams mParams;

    public LocatingPanel(Context context, OnSharedPreferenceChangeListener listener) {
        super(context);
        setBackgroundColor(Color.argb(0x55, 0x55, 0x55, 0x55));

//        mParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        mParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ScreenUtils.getScreenHeight() * 2 / 5);
        mParams.gravity = Gravity.BOTTOM;

        // 添加view
        TabLayout tabs = new TabLayout(context);
        tabs.setId(ID_TABS);
        LayoutParams tabsParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        tabsParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        tabsParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        tabs.setLayoutParams(tabsParams);
        addView(tabs);

        ViewPager pager = new ViewPager(context);
        LayoutParams pagerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        pagerParams.addRule(RelativeLayout.BELOW, ID_TABS);
        pager.setLayoutParams(pagerParams);
        addView(pager);

        // 初始化
        PanelAdapter adapter = new PanelAdapter();
        List<PanelView> data = new ArrayList<>();
        CommonSettingPanel common = new CommonSettingPanel(context);
        common.setConfigChangeListener(listener);
        data.add(common);
        data.add(new AlgorithmSettingPanel(context));
        adapter.setData(data);
        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);

    }

    public void hide() {
        if(isShown()) {
            ((ViewGroup)getParent()).removeView(this);
        }
    }

    public void show(Window window) {
        if(!isShown()) {
            window.addContentView(this, mParams);
        }
    }
}
