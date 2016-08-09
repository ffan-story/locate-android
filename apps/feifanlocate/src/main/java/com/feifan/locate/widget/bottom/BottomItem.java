package com.feifan.locate.widget.bottom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatDrawableManager;

/**
 * 底部栏项
 * <p>
 *     描述底部栏中的一项
 * </p>
 * Created by xuchunlei on 16/8/3.
 */
public class BottomItem {

    /** 图标 */
    private Drawable mIcon;

    /** 文本 */
    private String mTitle;

    /** 资源 */
    public int iconRes;
    public int titleRes;

    public BottomItem(@DrawableRes int iconRes, @NonNull  int titleRes) {
        this.titleRes = titleRes;
        this.iconRes = iconRes;
    }

    /** 获取图标 */
    public Drawable getIcon(Context context) {
        if(mIcon == null) {
            if(iconRes != 0) {
                mIcon = AppCompatDrawableManager.get().getDrawable(context, iconRes);
            }
        }
        return mIcon;
    }

    /** 获取标题 */
    public String getTitle(Context context) {
        if(mTitle == null) {
            if(titleRes != 0) {
                mTitle = context.getString(titleRes);
            }
        }
        return mTitle;
    }

    @Override
    public String toString() {
        return "titleRes = " + titleRes + ",iconRes = " + iconRes;
    }
}
