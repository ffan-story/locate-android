package com.feifan.locate;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.feifan.locate.provider.ProviderHelper;
import com.feifan.locate.widget.bottom.BottomBarLayout;

import java.util.concurrent.Executors;

import com.networkbench.agent.impl.NBSAppAgent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 添加BottomBarLayout
        final BottomBarLayout barLayout = BottomBarLayout.attach(this);
        barLayout.setTabs(R.xml.main_bottom_bar_tabs);

        loadData();

        // 听云
        NBSAppAgent.setLicenseKey("df2d2dbb298442df85130003ec659578").
                withLocationServiceEnabled(true).start(this.getApplicationContext());
    }

    private void loadData() {
        if(!LocatePreferences.getInstance().getInitialFlag()) {
            ProviderHelper.runOnWorkerThread(new Runnable() {
                @Override
                public void run() {
                    ContentResolver resolver = getContentResolver();
                    MockServer.requestBuildingData(resolver);
                    MockServer.requestZoneData(resolver);
                }
            });

            LocatePreferences.getInstance().setInitialFlag(true);

        }
    }
}
