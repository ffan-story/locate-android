package com.feifan.locate;

import android.content.ContentResolver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.feifan.debuglib.window.DebugWindow;
import com.feifan.locate.provider.ProviderHelper;
import com.feifan.locate.widget.bottom.BottomBarLayout;
import com.networkbench.agent.impl.NBSAppAgent;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import rx.internal.schedulers.ExecutorScheduler;

public class MainActivity extends AppCompatActivity {

    private ScheduledExecutorService mExecutor;

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

        // debug
        DebugWindow.get().initialize(this);
        mExecutor = Executors.newScheduledThreadPool(5);
        mExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try{
                    //                Log.e("MainActivity", System.currentTimeMillis() + ":Just a test form debug window");
                    DebugWindow.get().logI(System.currentTimeMillis() + ":Info test form debug window");
                    DebugWindow.get().logE(System.currentTimeMillis() + ":Error test form debug window");
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, 0, 1, TimeUnit.SECONDS);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mExecutor != null) {
            mExecutor.shutdownNow();
        }
        DebugWindow.get().destory(this);
    }

    private void loadData() {
        if(!LocatePreferences.getInstance().getInitialFlag()) {
            ProviderHelper.runOnWorkerThread(new Runnable() {
                @Override
                public void run() {
                    MockServer.requestBuildingData(MainActivity.this);
                    MockServer.requestZoneData(MainActivity.this);

                    MockServer.createImapForRtMap(MainActivity.this);
                }
            });

            LocatePreferences.getInstance().setInitialFlag(true);

        }
    }
}
