package com.libs.ui.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by mengmeng on 15/12/1.
 */
public abstract class AbsToolBarDelegate {
    /*上下文，创建view的时候需要用到*/
    protected Context mContext;

    /*base view*/
    protected View mContentView;

    protected View mBackView;

    protected FrameLayout mExtraView;

    /*视图构造器*/
    protected LayoutInflater mInflater;

    public AbsToolBarDelegate(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public abstract View getContentView();
    public abstract View getBackView();
    public abstract ViewGroup getExtraView();
    public abstract void addRightView(View view);
    public abstract View getTitleView();
}
