package com.feifan.locate;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.feifan.locate.sampling.SpotPlanFragment;

public class TitleBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_bar);

        // 临时代码
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.title_bar_container, Fragment.instantiate(this, SpotPlanFragment.class.getName()))
                .commitAllowingStateLoss();
    }
}
