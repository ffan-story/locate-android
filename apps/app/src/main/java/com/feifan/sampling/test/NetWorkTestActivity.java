package com.feifan.sampling.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.feifan.sampling.R;

/**
 * 带有TopBar的活动
 */
public class NetWorkTestActivity extends AppCompatActivity {

    /** 参数名－fragment界面类名 */
    public final static String EXTRA_NAME_FRAGMENT = "fragment";
    /** 参数名－fragment参数 */
    public final static String EXTRA_NAME_ARGUMENTS = "arguments";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_bar);

        // 加载内容
        String fragmentName = getIntent().getStringExtra(EXTRA_NAME_FRAGMENT);
        if(fragmentName != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.top_bar_content_container, Fragment.instantiate(this, fragmentName, getIntent().getBundleExtra(EXTRA_NAME_ARGUMENTS)));
            transaction.commitAllowingStateLoss();
        }
    }
}
