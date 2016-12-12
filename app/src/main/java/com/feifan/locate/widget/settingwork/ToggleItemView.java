package com.feifan.locate.widget.settingwork;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.feifan.locate.R;

/**
 * Created by xuchunlei on 16/10/12.
 */

public class ToggleItemView extends RelativeLayout {

    @IdRes
    private static int ID_TITLE = 1;
    @IdRes
    private static int ID_TOGGLE = 2;

    public ToggleItemView(Context context) {
        this(context, null);
    }

    public ToggleItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context, attrs);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        ((ToggleButton)findViewById(ID_TOGGLE)).setOnCheckedChangeListener(listener);
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
        if(a.hasValue(R.styleable.SettingItem_showTitle)) {
            titleV.setText(a.getResourceId(R.styleable.SettingItem_showTitle, -1));
        }
        LayoutParams paramsTitle = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        paramsTitle.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        paramsTitle.addRule(RelativeLayout.CENTER_VERTICAL);
        addView(titleV, paramsTitle);

        // 开关
        ToggleButton toggleV = new ToggleButton(context);
        toggleV.setId(ID_TOGGLE);
        LayoutParams paramsToggle = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        paramsToggle.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramsToggle.addRule(RelativeLayout.CENTER_VERTICAL);
        addView(toggleV, paramsToggle);
    }
}
