package com.feifan.sampling;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.feifan.beacon.Beacon;
import com.feifan.beacon.BeaconConsumer;
import com.feifan.beacon.BeaconManager;
import com.feifan.beacon.BeaconParser;
import com.feifan.beacon.RangeNotifier;
import com.feifan.beacon.Region;
import com.feifan.sampling.provider.SampleData.BeaconDetail;
import com.feifan.sampling.provider.SampleData.BeaconUUID;
import com.feifan.sampling.provider.SampleData.Sample;
import com.feifan.sampling.sample.ExportDetailFragment;
import com.feifan.sampling.sample.SampleFragment;
import com.feifan.sampling.sample.SampleFragment.OnDataChangeListener;
import com.feifan.sampling.spot.SpotConstant;
import com.feifan.sampling.spot.SpotUploadService;
import com.feifan.sampling.util.ProviderUtil;
import com.libs.base.sensor.dici.DiciService;
import com.libs.utils.PrefUtil;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Beacon详情活动类
 * <pre>
 *     展示在某个采集点（spot）收到的beacon发出的数据信息
 * </pre>
 */
public class SampleActivity extends AppCompatActivity implements BeaconConsumer, OnDataChangeListener {

    private String TAG = SampleActivity.class.getSimpleName();

    private static final int SAMPLING_STATUS_UNCONNECTED = 1;
    private static final int SAMPLING_STATUS_CONNECTED = SAMPLING_STATUS_UNCONNECTED + 1;
    private static final int SAMPLING_STATUS_STARTED = SAMPLING_STATUS_CONNECTED + 1;
    private static final int SAMPLING_STATUS_PAUSED = SAMPLING_STATUS_STARTED + 1;
    private static final int SAMPLING_STATUS_STOPPED = SAMPLING_STATUS_PAUSED + 1;
    private DiciService mDiciService;
    // 采样
    private BeaconManager beaconManager;
    private int mStatus = SAMPLING_STATUS_CONNECTED;
    private Region mRegion = new Region("jindi-B-22-east-2", null, null, null);

    // 当前采集的样本个数
    private AtomicInteger mSampleCount = new AtomicInteger(0);
    // 采集点ID
    private int mSpotId;
    // 最大采样数量
    private int mMaxSampleCount = 100;

    // view
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        // 初始化系统参数
        mMaxSampleCount = Integer.valueOf(PrefUtil.getString(this,Constants.SHAREPREFERENCE.SCAN_MAX_COUNT,Constants.DEFAULT_SCAN_SAMPLES));
        Log.e(TAG, "MaxSampleCount-------->" + mMaxSampleCount);

        mSpotId = getIntent().getIntExtra(Constants.EXTRA_KEY_SPOT_ID, -1);
        final String spotName = getIntent().getStringExtra(Constants.EXTRA_KEY_SPOT_NAME);
        Log.d(TAG, "onCreate:we got spot'id is " + mSpotId + " from arguments");
        mDiciService = DiciService.getInstance(getApplicationContext());
        // 绑定beacon管理器
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(Constants.LAYOUT_IBEACON));
        long timeInterval = PrefUtil.getLong(getApplicationContext(),Constants.SHAREPREFERENCE.RECYCLE_TIME_INTERVAL,1100);
//        beaconManager.setBackgroundBetweenScanPeriod(timeInterval);
//        beaconManager.setForegroundBetweenScanPeriod(timeInterval);
        Toolbar toolbar = (Toolbar) findViewById(R.id.sample_toolbar);
        setSupportActionBar(toolbar);

        // 样本详情界面参数
        final Intent intent = new Intent(getApplicationContext(), TopBarActivity.class);
        intent.putExtra(TopBarActivity.EXTRA_NAME_FRAGMENT, ExportDetailFragment.class.getName());
        Bundle args = new Bundle();
        args.putInt(Constants.EXTRA_KEY_SPOT_ID, mSpotId);
        args.putString(Constants.EXTRA_KEY_SPOT_NAME, spotName);
        intent.putExtra(TopBarActivity.EXTRA_NAME_ARGUMENTS, args);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    switch (mStatus) {
                        case SAMPLING_STATUS_UNCONNECTED:
                            break;
                        case SAMPLING_STATUS_CONNECTED:
                        case SAMPLING_STATUS_PAUSED:
                            beaconManager.startRangingBeaconsInRegion(mRegion);
                            mStatus = SAMPLING_STATUS_STARTED;
                            mFab.setImageResource(android.R.drawable.ic_media_pause);
                            break;
                        case SAMPLING_STATUS_STARTED:
                            beaconManager.stopRangingBeaconsInRegion(mRegion);
                            mStatus = SAMPLING_STATUS_PAUSED;
                            mFab.setImageResource(android.R.drawable.ic_media_play);
                            break;
                        case SAMPLING_STATUS_STOPPED:
                            if(beaconManager.isBound(SampleActivity.this)){
                                beaconManager.stopRangingBeaconsInRegion(mRegion);
                            }
                            startActivity(intent);
                            break;

                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(Constants.EXTRA_KEY_SPOT_ID, mSpotId);
            arguments.putString(Constants.EXTRA_KEY_SPOT_NAME, spotName);
            SampleFragment fragment = new SampleFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.sample_container, fragment)
                    .commit();

            //初始化 TODO maybe need modify
//            Bundle sampleArgs = getIntent().getBundleExtra(AbsLoaderFragment.EXTRA_KEY_LOADER_ARGS);
//            mSpotId = sampleArgs.getInt(SampleFragment.EXTRA_KEY_SPOT_ID);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDiciService.startMagicScan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDiciService.stopMagicScan();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(beaconManager.isBound(this)) {
            beaconManager.unbind(this);
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                final double time = System.currentTimeMillis() * 0.001d;
                if(mSampleCount.get() >= mMaxSampleCount) { // 超过最大采样数量
                    mStatus = SAMPLING_STATUS_STOPPED;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mFab.setImageResource(android.R.drawable.ic_menu_upload);
                            mFab.callOnClick();
                        }
                    });
                    return;
                }
                final float fangxiang = mDiciService.getAzimuth();
                if (beacons.size() > 0) { // 样本中有数据
                    final Collection<Beacon> beaconData = beacons;
                    startUploadService(beaconData,time);
                    Log.d(TAG, "didRangeBeaconsInRegion:we will record the sample at " + time);
                    ProviderUtil.runOnWorkerThread(new Runnable() {
                        @Override
                        public void run() {
                            // 保存样本
                            ContentValues values = new ContentValues();
                            values.put(Sample.NAME, "sample#" + mSampleCount.getAndIncrement());
                            values.put(Sample.TIME, time);
                            values.put(Sample.SPOT, mSpotId);
                            Uri uri = getContentResolver().insert(Sample.CONTENT_URI, values);
                            Log.e(TAG, "a new sample is added to " + uri);
                            int sampleId = Integer.valueOf(uri.getLastPathSegment());
                            // 保存beacon信息
                            for(Beacon b : beaconData) {
                                String uuid = b.getId1().toString();

                                // 保存beacon的UUID
                                try{
                                    values.clear();
                                    values.put(BeaconUUID.UUID, uuid);
                                    Uri uriUUID = getContentResolver().insert(BeaconUUID.CONTENT_URI, values);
                                    Log.i(TAG, "a new com.my.com.my.com.mm.beacon's uuid is added to " + uriUUID);
                                }catch (SQLiteConstraintException e) {
                                    Log.w(TAG, "uuid(" + uuid + ") is duplicated");
                                }

                                // 保存beacon详情
                                values.clear();
                                values.put(BeaconDetail.UUID, uuid);
                                values.put(BeaconDetail.MAJOR, b.getId2().toString());
                                values.put(BeaconDetail.MINOR, b.getId3().toString());
                                values.put(BeaconDetail.RSSI, b.getRssi());
                                values.put(BeaconDetail.DIRECTION,String.valueOf(fangxiang));
                                values.put(BeaconDetail.ACCURACY, b.getDistance());
                                values.put(BeaconDetail.SAMPLE, sampleId);
                                Uri uriDetails = getContentResolver().insert(BeaconDetail.CONTENT_URI, values);
                                Log.i(TAG, "a new com.my.com.my.com.mm.beacon's details is added to " + uriDetails);
                            }
                        }
                    });

                } else { // 用于测试，勿删



//                    // 保存样本
//                    ContentValues values = new ContentValues();
//                    values.put(Sample.NAME, "sample#" + mSampleCount);
//                    values.put(Sample.SPOT, mSpotId);
//                    Uri uri = getContentResolver().insert(Sample.CONTENT_URI, values);
//                    mSampleCount++;
//                    Log.e(TAG, "a new sample is added to " + uri);
//
//                    // 保存beacon详情
//                    int sampleId = Integer.valueOf(uri.getLastPathSegment());
//                    values.clear();
//                    values.put(BeaconDetail.UUID, "1111-2222-3333-4444");
//                    values.put(BeaconDetail.MAJOR, "11");
//                    values.put(BeaconDetail.MINOR, "22");
//                    values.put(BeaconDetail.RSSI, "33");
//                    values.put(BeaconDetail.SAMPLE, sampleId);
//                    Uri uriDetails = getContentResolver().insert(BeaconDetail.CONTENT_URI, values);
//                    Log.i(TAG, "a new com.my.com.my.com.mm.beacon's details is added to " + uriDetails);

                }
            }
        });
        mStatus = SAMPLING_STATUS_CONNECTED;
    }

    private void startUploadService(Collection<Beacon> beaconData,double time){
        if(beaconData == null || beaconData.isEmpty()){
            return;
        }
        // 保存beacon信息
        for(Beacon b : beaconData) {
            String uuid = b.getId1().toString();
            Intent intent=new Intent(SampleActivity.this,SpotUploadService.class);
            Bundle bundle = new Bundle();
            bundle.putString(SpotConstant.UUID,uuid);
            bundle.putInt(SpotConstant.MAJOR, b.getId2().toInt());
            bundle.putInt(SpotConstant.MINOR,b.getId3().toInt());
            bundle.putInt(SpotConstant.RSSI,b.getRssi());
            bundle.putLong(SpotConstant.TIME, (long) time);
            bundle.putInt(SpotConstant.SPOT_ID,mSpotId);
            intent.putExtras(bundle);
            startService(intent);
        }
    }
    @Override
    public void onNotifyChange(int totalSize) {
        mSampleCount.set(totalSize);
        if(totalSize >= mMaxSampleCount) {
            mStatus = SAMPLING_STATUS_STOPPED;
            mFab.setImageResource(android.R.drawable.ic_menu_upload);
        }else { // 样本数目未达到最大采样数量
            if(mStatus != SAMPLING_STATUS_STARTED){ // 开始状态下，不进行绑定
                beaconManager.bind(this);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
