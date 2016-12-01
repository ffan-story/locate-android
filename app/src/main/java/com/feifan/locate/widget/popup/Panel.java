package com.feifan.locate.widget.popup;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.feifan.baselib.utils.LogUtils;

/**
 * Created by xuchunlei on 2016/11/23.
 */

public abstract class Panel extends LinearLayout {

    private FrameLayout.LayoutParams mParams;

    /**
     * 通过程序动态创建时调用此构造方法
     * @param context
     */
    public Panel(Context context) {
        this(context, null);
        initView(context);
    }

    /**
     * 通过xml布局文件inflate时调用此构造方法
     * @param context
     * @param attrs
     */
    public Panel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Panel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(Color.argb(0x55, 0x55, 0x55, 0x55));
        setOrientation(VERTICAL);
        mParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        onInit(context, mParams);
    }

    public void show(Window container) {
        container.addContentView(this, mParams);
    }

    public void hide() {
        if(isAttachedToWindow()) {
            ((ViewGroup)getParent()).removeView(this);
        }
    }

    public boolean isShown() {
        return isAttachedToWindow();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView(getContext());
    }

    protected abstract void onInit(Context context, FrameLayout.LayoutParams params);
    protected abstract void initView(Context context);

    /**
     * 查找到指定ID的视图
     * @param id
     * @param <T>
     * @return
     */
    protected <T> T findView(@IdRes int id) {
        return (T)findViewById(id);
    }
}
