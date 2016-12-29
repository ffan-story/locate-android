package com.feifan.locate.widget.settingwork;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feifan.locate.R;

/**
 * Created by xuchunlei on 2016/12/7.
 */

public class CheckItemView extends RelativeLayout {

    @IdRes
    private static final int ID_TITLE = 1;
    @IdRes
    private static final int ID_CHECK = 2;

    public CheckItemView(Context context) {
        this(context, null);
    }

    public CheckItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context, attrs);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        ((CheckBox)findViewById(ID_CHECK)).setOnCheckedChangeListener(listener);
    }

    private void initViews(Context context, AttributeSet attrs) {

        if(attrs == null) {
            throw new IllegalStateException("you should set attribs for item");
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingItem);

        // 图标和标题
        TextView titleV = new TextView(context);
        titleV.setId(ID_TITLE);
        titleV.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.setting_item_title_size));
        titleV.setTextColor(ContextCompat.getColor(context, R.color.setting_item_title_text));
        if(a.hasValue(R.styleable.SettingItem_settingTitle)) {
            titleV.setText(a.getResourceId(R.styleable.SettingItem_settingTitle, -1));
        }
        LayoutParams paramsTitle = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        paramsTitle.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        paramsTitle.addRule(RelativeLayout.CENTER_VERTICAL);
        addView(titleV, paramsTitle);

        // 开关
        CheckBox checkV = new CheckBox(context);
        checkV.setId(ID_CHECK);
        LayoutParams paramsCheck = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        paramsCheck.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramsCheck.addRule(RelativeLayout.CENTER_VERTICAL);
        addView(checkV, paramsCheck);
    }
}
