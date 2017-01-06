package com.feifan.locate.locating.config;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * Created by xuchunlei on 2017/1/3.
 */

public abstract class PanelView extends ScrollView implements IPanel{

    private RelativeLayout mContainer;

    public PanelView(Context context) {
        super(context);

        mContainer = new RelativeLayout(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mContainer, params);
    }

    @Override
    public void addView(View child) {
        mContainer.addView(child);
    }
}
