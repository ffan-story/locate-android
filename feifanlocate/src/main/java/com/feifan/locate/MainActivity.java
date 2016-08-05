package com.feifan.locate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.feifan.locate.widget.BottomBarLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 添加BottomBar
        final BottomBarLayout barLayout = BottomBarLayout.attach(this);
        barLayout.setTabs(R.xml.main_bottom_bar_tabs);
    }
}
