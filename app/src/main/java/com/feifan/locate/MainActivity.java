package com.feifan.locate;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.feifan.baselib.BuildConfig;
import com.feifan.locate.provider.LocateData.Zone;
import com.feifan.locate.widget.bottom.BottomBarLayout;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String SHARED_PREFERENCES_KEY = "com.feifan.locate.prefs";
    private static final String INITIALIZE_DATA_FLAG = "INITIALIZE_DATA_FLAG";
    private SharedPreferences mSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPrefs = getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);

        // 添加BottomBarLayout
        final BottomBarLayout barLayout = BottomBarLayout.attach(this);
        barLayout.setTabs(R.xml.main_bottom_bar_tabs);

        if(!mSharedPrefs.getBoolean(INITIALIZE_DATA_FLAG, false)) {

            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    ContentResolver resolver = getContentResolver();

                    ContentValues values = new ContentValues();
                    values.put(Zone._ID, 1);
                    values.put(Zone.NAME, "金地中心B座31层");
                    values.put(Zone.PLAN, "zone_jindi_b31.png");
                    values.put(Zone.SCALE, 0.1);
                    resolver.insert(Zone.CONTENT_URI, values);

                    values.clear();
                    values.put(Zone._ID, 2);
                    values.put(Zone.NAME, "石景山万达广场F1");
                    values.put(Zone.PLAN, "zone_shijingshan_f1.jpg");
                    values.put(Zone.SCALE, 0.136);
                    resolver.insert(Zone.CONTENT_URI, values);

                    values.clear();
                    values.put(Zone._ID, 3);
                    values.put(Zone.NAME, "石景山万达广场F2");
                    values.put(Zone.PLAN, "zone_shijingshan_f2.jpg");
                    values.put(Zone.SCALE, 0.136);
                    resolver.insert(Zone.CONTENT_URI, values);

                    values.clear();
                    values.put(Zone._ID, 4);
                    values.put(Zone.NAME, "石景山万达广场F3");
                    values.put(Zone.PLAN, "zone_shijingshan_f3.jpg");
                    values.put(Zone.SCALE, 0.136);
                    resolver.insert(Zone.CONTENT_URI, values);

                    values.clear();
                    values.put(Zone._ID, 5);
                    values.put(Zone.NAME, "石景山万达广场B1");
                    values.put(Zone.PLAN, "zone_shijingshan_b1.jpg");
                    values.put(Zone.SCALE, 0.226);
                    resolver.insert(Zone.CONTENT_URI, values);

                    values.clear();
                    values.put(Zone._ID, 6);
                    values.put(Zone.NAME, "石景山万达广场B2");
                    values.put(Zone.PLAN, "zone_shijingshan_b2.jpg");
                    values.put(Zone.SCALE, 0.226);
                    resolver.insert(Zone.CONTENT_URI, values);
                }
            });

            mSharedPrefs.edit().putBoolean(INITIALIZE_DATA_FLAG, true).apply();

        }
    }
}
