package com.mm.beacon.blue;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mengmeng on 15/8/21.
 */
public abstract class IBlueManager {
  protected Context mContext;
  protected List<OnBlueScanListener> mScanListnerList = new ArrayList<OnBlueScanListener>();
  public IBlueManager(Context context) {
    mContext = context;
  }

  public abstract void startScan();

  public abstract void stopScan();

  public void registerListener(OnBlueScanListener listener){
    if(listener != null && !mScanListnerList.contains(listener)){
      mScanListnerList.add(listener);
    }
  }

  public void unRegisterListener(OnBlueScanListener listener){
    if(listener != null && mScanListnerList.contains(listener)){
      mScanListnerList.remove(listener);
    }
  }

  public void notifyScanListener( ScanData scanData){
    if(scanData != null){
      if(mScanListnerList != null && !mScanListnerList.isEmpty()){
        for (OnBlueScanListener listener: mScanListnerList) {
          if(listener != null){
            listener.onBlueScan(scanData);
          }
        }
      }
    }
  }

    public interface OnBlueScanListener{
        public void onBlueScan( ScanData scanData);
    }
}
