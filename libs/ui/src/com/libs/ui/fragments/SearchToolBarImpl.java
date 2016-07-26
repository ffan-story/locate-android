package com.libs.ui.fragments;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mengmeng on 15/12/1.
 */
public class SearchToolBarImpl extends AbsToolBarDelegate{

    public SearchToolBarImpl(Context context) {
        super(context);
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public View getBackView() {
        return null;
    }

    @Override
    public ViewGroup getExtraView() {
        return null;
    }

    @Override
    public void addRightView(View view) {

    }

    @Override
    public View getTitleView() {
        return null;
    }
}
