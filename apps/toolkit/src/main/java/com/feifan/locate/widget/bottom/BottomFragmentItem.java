package com.feifan.locate.widget.bottom;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

/**
 * 关联Fragment界面的标签项
 *
 * Created by xuchunlei on 16/8/5.
 */
public class BottomFragmentItem extends BottomItem {

    private Fragment mFragment;
    public String fragmentName;
    private Bundle mArgs;

    public BottomFragmentItem(@DrawableRes int iconRes, @NonNull int titleRes, String fragmentName, Bundle args) {
        super(iconRes, titleRes);
        this.fragmentName = fragmentName;
        this.mArgs = args;
    }

    public Fragment getFragment(Context context) {
        if(mFragment == null) {
            if(fragmentName != null) {
                mFragment = Fragment.instantiate(context, fragmentName, mArgs);
            }
        }
        return mFragment;
    }

    @Override
    public String toString() {
        return super.toString() + ",fragmentName = " + fragmentName;
    }
}
