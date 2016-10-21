package com.feifan.locate;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.feifan.locate.provider.ProviderHelper;
import com.feifan.locate.widget.bottom.BottomBarLayout;

import java.util.concurrent.Executors;

import com.networkbench.agent.impl.NBSAppAgent;

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
            ProviderHelper.runOnWorkerThread(new Runnable() {
                @Override
                public void run() {
                    ContentResolver resolver = getContentResolver();
                    MockServer.requestBuildingData(resolver);
                    MockServer.requestZoneData(resolver);
                }
            });

            mSharedPrefs.edit().putBoolean(INITIALIZE_DATA_FLAG, true).apply();

        }

        // 听云
        NBSAppAgent.setLicenseKey("df2d2dbb298442df85130003ec659578").
                withLocationServiceEnabled(true).start(this.getApplicationContext());
    }
}
