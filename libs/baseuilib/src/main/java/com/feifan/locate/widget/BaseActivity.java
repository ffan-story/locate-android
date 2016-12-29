package com.feifan.locate.widget;

import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by xuchunlei on 16/8/22.
 */
public class BaseActivity extends AppCompatActivity {

    /**
     * 查找View
     * @param id
     * @param <T>
     * @return
     */
    protected <T> T findView(@IdRes int id) {
         View v = findViewById(id);
        if(v != null) {
            return (T)v;
        }
        return null;
    }
}
