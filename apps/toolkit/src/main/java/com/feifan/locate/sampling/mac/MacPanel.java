package com.feifan.locate.sampling.mac;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.feifan.baselib.utils.IOUtils;
import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.widget.popup.Panel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by xuchunlei on 2016/11/23.
 */

public class MacPanel extends Panel {

    public MacPanel(Context context) {
        super(context);
    }

    public MacPanel(Context context, AttributeSet attrs) {
        super(context);
    }

    public MacPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit(Context context, FrameLayout.LayoutParams params) {
        params.gravity = Gravity.TOP;
    }

    @Override
    protected void initView(Context context) {
        RadioGroup radioGroup = new RadioGroup(context);
        InputStream is = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        try {
            // 获取合法的定位键信息
            is = context.getAssets().open("uuid.key");
            reader = new InputStreamReader(is);
            bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                RadioButton rb = new RadioButton(context);
                rb.setText(line);
                radioGroup.addView(rb);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(bufferedReader);
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(is);
        }
        addView(radioGroup);
    }
}
