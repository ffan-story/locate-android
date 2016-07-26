package com.mm.beacon.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.mm.beacon.BeaconDispatcher;
import com.mm.beacon.BeaconFilter;
import com.mm.beacon.IBeacon;
import com.mm.beacon.RegionFilter;
import com.mm.beacon.blue.BlueLOLLIPOPManager;
import com.mm.beacon.blue.BlueLeManager;
import com.mm.beacon.blue.IBlueManager;
import com.mm.beacon.blue.ScanData;
import com.mm.beacon.FilterBeacon;
import com.mm.beacon.data.Region;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by mengmeng on 15/8/20.
 */
public class BeaconService extends Service implements IBlueManager.OnBlueScanListener {
  public static final String TAG = "BeaconService";
  private boolean isScanning;
  private Handler mHandler = new Handler();
  private BeaconBinder mBeaconBinder = new BeaconBinder();
  private List<IBeacon> mBeaconList = new ArrayList<IBeacon>();
  private boolean mIsBind = false;
  private IBlueManager mBlueManager;
  private  BeaconFilter mBeaconFilter = new  BeaconFilter();
  private  RegionFilter mRegionFilter = new  RegionFilter();
  private  BeaconDispatcher mBeaconDisptcher;
  private List<IBeacon> mBeaconRegionMatchList = new ArrayList<IBeacon>();
  private List<ScanData> mTempScanDataList = new ArrayList<ScanData>();
  private long mScanDelay = 1500;
  private Runnable mBeaconRunnable = new Runnable() {
    @Override
    public void run() {
      processBeaconResult();
    }
  };

  @Override
  public void onBlueScan(ScanData scanData) {
    if (isBind() && isScanning()) {
      if (scanData != null) {
        scanData.time = System.currentTimeMillis();
        mTempScanDataList.add(scanData);
        processResult(scanData);
      }
    }
  }

  /**
   * Class used for the client Binder. Because we know this service always
   * runs in the same process as its clients, we don't need to deal with IPC.
   */
  public class BeaconBinder extends Binder {
    public BeaconService getService() {
      Log.i(TAG, "getService of IBeaconBinder called");
      return BeaconService.this;
    }
  }



  private boolean isBind() {
    return mIsBind;
  }

  private void setBind(boolean mIsBind) {
    this.mIsBind = mIsBind;
  }

  public boolean isScanning() {
    return isScanning;
  }

  public void setIsScanning(boolean isScanning) {
    this.isScanning = isScanning;
  }

  private void processResult(ScanData scanData) {
    if (scanData != null) {
      IBeacon iBeacon = IBeacon.fromScanData(scanData);
      if (iBeacon == null) {
        return;
      }
      if(!isBeaconMatched(iBeacon)){
        return;
      }
      // 判断是否进店
      matchRegions(iBeacon, mRegionFilter.getRegionList());
      //TODO 暂时去掉重复
//      if (mBeaconList.contains(iBeacon)) {
//        mBeaconList.remove(iBeacon);
//      }
//      int matNum = matchBeacons(iBeacon, mBeaconList);
//      if (matNum > 0) {
//        IBeacon beacon = mBeaconList.remove(matNum);
//        iBeacon.copyBeaconInsideNum(beacon);
//      }
      mBeaconList.add(iBeacon);
    }
  }

  public void setScanDelay(int delay){
    if (delay > 0){
      mScanDelay = delay;
    }
  }

  private void processBeaconResult() {
    // 处理进店出店的判断
//    performRegin();
    if (mBeaconDisptcher != null && !mBeaconList.isEmpty()) {
      mBeaconDisptcher.onBeaconDetect(mBeaconList);
      mBeaconList.clear();
    } else if (mBeaconDisptcher != null) {
      mBeaconDisptcher.onBeaconDetect(null);
    }
    performRawData();
//    stopBeaconScan();
    mHandler.postDelayed(mBeaconRunnable, mScanDelay);
  }

  private void performRawData(){
    if(mTempScanDataList.size() >0){
      mBeaconDisptcher.onBeaconRawDataDetect(mTempScanDataList);
      mTempScanDataList.clear();
    }
  }

  private void performRegin() {
    if (mBeaconList != null && !mBeaconList.isEmpty()) {
      List<Region> list = mRegionFilter.getRegionList();
      if (list != null && !list.isEmpty()) {
        for (int i = 0; i < list.size(); i++) {
          Region region = list.get(i);
          if (region != null) {
            boolean matched = matchRegions(mBeaconList, region);
            if (!matched) {
              boolean isInside = region.isInside();
              region.subRegion();
              if (!region.isInside() && isInside && mBeaconDisptcher != null) { // 上一次是在室内，而此时是在室外
                mBeaconDisptcher.onBeaconExit(region);
              }
            } else {
              region.plusRegion();
            }
          }
        }
        return;
      }
    } else {
      List<Region> list = mRegionFilter.getRegionList();
      if (list != null && !list.isEmpty()) {
        for (int i = 0; i < list.size(); i++) {
          Region region = list.get(i);
          if (region != null) {
            boolean isInside = region.isInside();
            region.subRegion();
            if (!region.isInside() && isInside && mBeaconDisptcher != null) { // 上一次是在室内，而此时是在室外
              mBeaconDisptcher.onBeaconExit(region);
            }
          } else {
            region.plusRegion();
          }
        }
        return;
      }
    }
  }

  public void setBeaconDisptcher(BeaconDispatcher beaconDisptcher) {
    if (beaconDisptcher != null) {
      mBeaconDisptcher = beaconDisptcher;
    }
  }

  /**
   * When binding to the service, we return an interface to our messenger
   * for sending messages to the service.
   */
  @Override
  public IBinder onBind(Intent intent) {
    Log.i(TAG, "binding");
    createBlueManager();
    mBlueManager.registerListener(this);
    setBind(true);
    startBeaconScan();
    return mBeaconBinder;
  }

  private void createBlueManager(){
    int api = Build.VERSION.SDK_INT;
    if(api >= Build.VERSION_CODES.LOLLIPOP){
      mBlueManager = BlueLOLLIPOPManager.getInstance(this);
    }else if (api >= Build.VERSION_CODES.JELLY_BEAN_MR2){
      mBlueManager = BlueLeManager.getInstance(this);
    }
  }
  @Override
  public boolean onUnbind(Intent intent) {
    Log.i(TAG, "unbind called");
    setBind(false);
    mBlueManager.unRegisterListener(this);
    stopBeaconScan();
    return false;
  }


  @Override
  public void onCreate() {
    Log.i(TAG, "onCreate of IBeaconService called");
  }

  @Override
  public void onDestroy() {
    Log.i(TAG, "onDestory called.  stopping scanning");
    stopBeaconScan();
  }

  public void setRegionFilter(RegionFilter regionFilter) {
    if (regionFilter != null && !regionFilter.isEmpty()) {
      mRegionFilter = regionFilter;
    }
  }

  public void setBeaconFilter(BeaconFilter beaconfilter){
    if (beaconfilter != null && !beaconfilter.isEmpty()) {
      mBeaconFilter = beaconfilter;
    }
  }

  public void stopBeaconScan() {
    if (mBlueManager != null) {
      mBlueManager.stopScan();
    }
    mHandler.removeCallbacks(mBeaconRunnable);
    setIsScanning(false);
  }


  public void startBeaconScan() {
    if (isBind() && !isScanning()) {
      setIsScanning(true);
      if (mBlueManager != null) {
        mBlueManager.startScan();
        mHandler.postDelayed(mBeaconRunnable, mScanDelay);
      }
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
        if (!region.isInside()) {
          region.makeRegionInside();
          mBeaconDisptcher.onBeaconEnter(region);
        } else {
          region.makeRegionInside();
        }
        return true;
      }
    }
    return false;
  }

  private boolean matchRegions(List<IBeacon> beaconList, Region regions) {
    if (regions == null || beaconList == null || beaconList.isEmpty()) {
      return false;
    }
    for (int i = 0; i < beaconList.size(); i++) {
      IBeacon iBeacon = beaconList.get(i);
      if (iBeacon != null && matchRegion(iBeacon, regions)) {
        regions.makeRegionInside();
        return true;
      }
    }
    // 没有匹配
    regions.subRegion();
    return false;
  }

  private boolean matchRegion(IBeacon beacon, Region region) {
    if (beacon != null && region != null && region.matchesIBeacon(beacon)) {
      return true;
    }
    return false;
  }

  /**
   * check if beaconlist contains iBeacon
   * @param iBeacon
   * @param beacon
   * @return
   */
  private int matchBeacons(IBeacon iBeacon, List<IBeacon> beacon) {
    if (beacon == null || beacon.isEmpty()) {
      return -1;
    }
    Iterator<IBeacon> regionIterator = beacon.iterator();
    int i = 0;
    while (regionIterator.hasNext()) {
      IBeacon region = regionIterator.next();
      if (region.equals(iBeacon)) {
        return i;
      }
    }
    return -1;
  }

 private boolean isBeaconMatched(IBeacon iBeacon){
   List<FilterBeacon> beaconList = mBeaconFilter.getBeaconList();
   if(beaconList == null || beaconList.isEmpty()){
     return true;
   }
   if(iBeacon != null){
    if(beaconList != null && !beaconList.isEmpty()){
      for (int i = 0; i < beaconList.size(); i++) {
        FilterBeacon beacon = beaconList.get(i);
        if(beacon != null){
            if(isBeaconMatched(beacon,iBeacon)){
              return true;
            }
        }
      }
    }
   }
   return false;
 }

  private boolean isBeaconMatched(FilterBeacon filterBeacon , IBeacon ibeacon){
    if(filterBeacon != null && ibeacon != null){
      if(!filterBeacon.getUuid().equals(ibeacon.getProximityUuid())){
        return false;
      }
      if(filterBeacon.getMajor() != 0 && filterBeacon.getMajor() != ibeacon.getMajor()){
        return false;
      }
      if(filterBeacon.getMinor() != 0 && filterBeacon.getMinor() != ibeacon.getMinor()){
        return false;
      }
      return true;
    }
    return false;
  }
}
