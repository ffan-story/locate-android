package com.feifan.locate;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.feifan.locate.provider.LocateData;
import com.feifan.locate.widget.bottom.BottomBarLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        // 添加BottomBarLayout
        final BottomBarLayout barLayout = BottomBarLayout.attach(this);
        barLayout.setTabs(R.xml.main_bottom_bar_tabs);

//        LogUtils.e("1111111111111111111111111111111111111111111111111111111111111110--->"
//                + Long.parseLong("1000000000000000000000000000000000000000000000000000000000000001", 2));

//         临时代码－加载数据
        ContentResolver resolver = getContentResolver();
//
//        ContentValues values1 = new ContentValues();
//        values1.put(LocateData.Zone._ID, 1);
//        values1.put(LocateData.Zone.NAME, "金地中心A座22层东2办公区");
//        resolver.insert(LocateData.Zone.CONTENT_URI, values1);
//
//        ContentValues values2 = new ContentValues();
//        values2.put(LocateData.Zone._ID, 1);
//        values2.put(LocateData.Zone.NAME, "金地中心B座31层办公区");
//        resolver.insert(LocateData.Zone.CONTENT_URI, values2);
    }
}
