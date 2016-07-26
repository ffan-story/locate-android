
package com.mm.beacon.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.mm.beacon.BeaconDispatcher;
import com.mm.beacon.BeaconFilter;
import com.mm.beacon.IBeacon;
import com.mm.beacon.RegionFilter;
import com.mm.beacon.blue.ScanData;
import com.mm.beacon.data.Region;
import com.mm.beacon.service.BeaconService;

import java.util.ArrayList;
import java.util.List;

public class BeaconRemoteManager implements BeaconDispatcher {

  private static final String TAG = "IBeaconManager";
  private Context mContext;
  private boolean isServiceConnected = false;
  private static BeaconRemoteManager client = null;
  private BeaconService mService;
  private BeaconFilter mBeaconfilter;
  private RegionFilter mRegionFilter;
  private List<OnBeaconDetectListener> mBeaconDetectList = new ArrayList<OnBeaconDetectListener>();
  private int mDelay = 1000;

  /**
   * An accessor for the singleton instance of this class. A context must be provided, but if you
   * need to use it from
   * a non-Activity or non-Service class, you can attach it to another singleton or a subclass of
   * the Android
   * Application class.
   */
  public static BeaconRemoteManager getInstance(Context context) {
    if (!isInstantiated()) {
      Log.d(TAG, "IBeaconManager instance craetion");
      client = new BeaconRemoteManager(context);
    }
    return client;
  }


  /**
   * Determines if the singleton has been constructed already. Useful for not overriding settings
   * set declaratively in
   * XML
   *
   * @return true, if the class has been constructed
   */
  public static boolean isInstantiated() {
    return (client != null);
  }

  private BeaconRemoteManager(Context context) {
    mContext = context;
    mBeaconfilter = new BeaconFilter();
  }

  public void setBeaconFilter(BeaconFilter beaconFilter) {
    if (beaconFilter != null) {
      mBeaconfilter = beaconFilter;
    }
  }

  public void setRegionFilter( RegionFilter regionFilter) {
    if (regionFilter != null && !regionFilter.isEmpty()) {
      mRegionFilter = regionFilter;
    }
  }

  public void startService() {
    bindService();
  }

  public void stopService() {
    unBindService();
  }

  public void setDelay(int delay){
    if(delay > 0) {
      mDelay = delay;
    }
  }

  private ServiceConnection iBeaconServiceConnection = new ServiceConnection() {
    // Called when the connection with the service is established
    public void onServiceConnected(ComponentName className, IBinder service) {
      Log.d(TAG, "we have a connection to the service now");
      mService = ((BeaconService.BeaconBinder) service).getService();
      mService.setBeaconDisptcher(BeaconRemoteManager.this);
      isServiceConnected = true;
      mService.setScanDelay(mDelay);
      mService.setRegionFilter(mRegionFilter);
      mService.setBeaconFilter(mBeaconfilter);
    }

    // Called when the connection with the service disconnects unexpectedly
    public void onServiceDisconnected(ComponentName className) {
      mService.setBeaconDisptcher(null);
      isServiceConnected = false;
    }
  };

  private void bindService() {
    if (mContext != null) {
      Intent intent = new Intent(mContext.getApplicationContext(), BeaconService.class);
      mContext.bindService(intent, iBeaconServiceConnection, Context.BIND_AUTO_CREATE);
    }
  }

  private void unBindService() {
    if (mContext != null && isServiceConnected) {
      mContext.unbindService(iBeaconServiceConnection);
      isServiceConnected = false;
    }
    clearListener();
  }

  public void registerBeaconListerner(OnBeaconDetectListener listener) {
    if (mBeaconDetectList != null && !mBeaconDetectList.contains(listener)) {
      mBeaconDetectList.add(listener);
    }
  }

  public void unRegisterBeaconListener(OnBeaconDetectListener listener) {
    if (mBeaconDetectList != null && mBeaconDetectList.contains(listener)) {
      mBeaconDetectList.remove(listener);
    }
  }

  private void clearListener() {
    if (mBeaconDetectList != null && !mBeaconDetectList.isEmpty()) {
      mBeaconDetectList.clear();
    }
  }

  public void onDestory() {
    unBindService();
  }

  @Override
  public void onBeaconDetect(List<IBeacon> beaconlist) {
    if (mBeaconDetectList != null && beaconlist != null
        && !beaconlist.isEmpty()) {
      for (int i = 0; i < mBeaconDetectList.size(); i++) {
        OnBeaconDetectListener beacon = mBeaconDetectList.get(i);
        if (beacon != null) {
          List<IBeacon> list = new ArrayList<IBeacon>();
          list.addAll(beaconlist);
          printBeacons(list);
          beacon.onBeaconDetected(list);
        }
      }
    }
  }

  @Override
  public void onBeaconRawDataDetect(List<ScanData> beaconlist) {
    if(beaconlist != null && beaconlist.size() > 0){
      if (mBeaconDetectList != null && beaconlist != null
              && !beaconlist.isEmpty()) {
        for (int i = 0; i < mBeaconDetectList.size(); i++) {
          OnBeaconDetectListener beacon = mBeaconDetectList.get(i);
          if (beacon != null) {
            List<ScanData> list = new ArrayList<ScanData>();
            list.addAll(beaconlist);
            beacon.onBeaconRawDataDetect(list);
          }
        }
      }
    }
  }

  private void printBeacons(List<IBeacon> beacon) {
    if (beacon != null && !beacon.isEmpty()) {
      for (int i = 0; i < beacon.size(); i++) {
        if (beacon.get(i) != null
            && (Integer.valueOf(beacon.get(i).getMajor()) == 0 || Integer.valueOf(beacon.get(i)
                .getMajor()) == 21112)) {
//          Log.e(TAG, " major " + com.my.com.my.com.mm.beacon.get(i).getMajor()
//              + " minor " + com.my.com.my.com.mm.beacon.get(i).getMinor() + " rssi " + com.my.com.my.com.mm.beacon.get(i).getRssi() + " time "+com.my.com.my.com.mm.beacon.get(i).getTime());
        }
      }
    }
  }

  @Override
  public void onBeaconEnter(Region region) {
    if (mBeaconDetectList != null && !mBeaconDetectList.isEmpty()) {
      for (int i = 0; i < mBeaconDetectList.size(); i++) {
        OnBeaconDetectListener beacon = mBeaconDetectList.get(i);
        if (beacon != null) {
          beacon.onBeaconEnter(region);
        }
      }
    }
  }

  @Override
  public void onBeaconExit(Region region) {
    if (mBeaconDetectList != null && !mBeaconDetectList.isEmpty()) {
      for (int i = 0; i < mBeaconDetectList.size(); i++) {
        OnBeaconDetectListener beacon = mBeaconDetectList.get(i);
        if (beacon != null) {
          beacon.onBeaconExit(region);
        }
      }
    }
  }

  public interface OnBeaconDetectListener {
    public void onBeaconDetected(List<IBeacon> beaconlist);
    public void onBeaconRawDataDetect(List<ScanData> beaconlist);
    public void onBeaconEnter(Region region);

    public void onBeaconExit(Region region);
  }
}
