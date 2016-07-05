package com.mm.beacon.service;

import android.content.Context;
import android.os.Build;
import android.os.Handler;

import com.mm.beacon.BeaconConstants;
import com.mm.beacon.BeaconDispatcher;
import com.mm.beacon.BeaconFilter;
import com.mm.beacon.BeaconHelper;
import com.mm.beacon.IBeacon;
import com.mm.beacon.blue.BlueLOLLIPOPManager;
import com.mm.beacon.blue.BlueLeManager;
import com.mm.beacon.blue.IBlueManager;
import com.mm.beacon.blue.ScanData;
import com.mm.beacon.data.Region;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by mengmeng on 15/8/20.
 */
public class BeaconSingleScan implements IBlueManager.OnBlueScanListener {
    public static final String TAG = "BeaconService";
    private boolean mScanning = false;
    private Handler mHandler = new Handler();
    private static BeaconSingleScan mBeaconService;
    private IBlueManager mBlueManager;
    private BeaconDispatcher mBeaconDisptcher;
    private List<IBeacon> mBeaconList = new ArrayList<IBeacon>();
    private BeaconFilter mBeaconFilter;
    private Context mAppContext;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mBlueManager != null) {
                mBlueManager.stopScan();
                stopBeaconScan();
                sendBeaconList();
            }
        }
    };

    @Override
    public void onBlueScan(ScanData scanData) {
        if (scanData != null) {
            processResult(scanData);
        }
    }

    private BeaconSingleScan(Context context) {
        mAppContext = context.getApplicationContext();
        createBlueManager();
        mBlueManager.registerListener(this);
        mBeaconFilter = new BeaconFilter();
    }

    private void createBlueManager(){
        int api = Build.VERSION.SDK_INT;
        if(api >= Build.VERSION_CODES.LOLLIPOP){
            mBlueManager = BlueLOLLIPOPManager.getInstance(mAppContext);
        }else if (api >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            mBlueManager = BlueLeManager.getInstance(mAppContext);
        }
    }
    public void setBeaconFilter(BeaconFilter beaconFilter) {
        if (beaconFilter != null) {
            mBeaconFilter = beaconFilter;
        }
    }

    public static synchronized BeaconSingleScan getInstance(Context context) {
        if (mBeaconService == null) {
            mBeaconService = new BeaconSingleScan(context);
        }
        return mBeaconService;
    }

    /**
     * 开启服务
     */
    public void startBeaconScan() {
        if (!mScanning) {
            mScanning = true;
            if (mBlueManager != null) {
                mBeaconList.clear();
                mBlueManager.startScan();
                mHandler.postDelayed(mRunnable, BeaconConstants.SCAN_TIME);
            }
        }
    }

    private void processResult(ScanData scanData) {
        if (scanData != null) {
            IBeacon iBeacon = IBeacon.fromScanData(scanData.scanRecord, scanData.rssi);
            if (BeaconHelper.isBeaconMatched(iBeacon, mBeaconFilter)) {
                if (!matchRegions(iBeacon, mBeaconFilter.getFilterList())) {
                    return;
                }
                if (!mBeaconList.contains(iBeacon)) {
                    mBeaconList.add(iBeacon);
                }
            }
        }
    }

    private void sendBeaconList() {
        if (mBeaconList != null && !mBeaconList.isEmpty() && mBeaconDisptcher != null) {
            mBeaconDisptcher.onBeaconDetect(mBeaconList);
        } else if (mBeaconDisptcher != null) {
            mBeaconDisptcher.onBeaconDetect(null);
        }
    }

    public void setBeaconDisptcher(BeaconDispatcher beaconDisptcher) {
        if (beaconDisptcher != null) {
            mBeaconDisptcher = beaconDisptcher;
        }
    }

    public void stopBeaconScan() {
        if (mBlueManager != null && mScanning) {
            mBlueManager.stopScan();
            mHandler.removeCallbacks(mRunnable);
        }
        mScanning = false;
    }

    public void onDestory() {
        stopBeaconScan();
        if (mBlueManager != null) {
            mBlueManager.unRegisterListener(this);
            mBlueManager = null;
        }
    }

    private boolean matchRegions(IBeacon iBeacon, List<Region> regions) {
        if (regions == null || regions.isEmpty()) {
            return true;
        }
        Iterator<Region> regionIterator = regions.iterator();
        while (regionIterator.hasNext()) {
            Region region = regionIterator.next();
            if (region.matchesIBeacon(iBeacon)) {
                return true;
            }
        }
        return false;
    }

}
