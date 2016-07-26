package com.mm.beacon.blue;

import android.annotation.TargetApi;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.os.Build;

import java.util.List;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class SyncScanCallback extends ScanCallback {
    private IBlueManager.OnBlueScanListener mOnBlueScanListener;
    public SyncScanCallback(IBlueManager.OnBlueScanListener listener) {
        if(listener != null){
            mOnBlueScanListener = listener;
        }else {
            throw new IllegalArgumentException("the listener can not be null");
        }
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        super.onScanResult(callbackType, result);
        ScanData data = parseScanResult(result);
        if(mOnBlueScanListener != null && data != null){
            mOnBlueScanListener.onBlueScan(data);
        }
    }

    private ScanData parseScanResult(ScanResult result) {
        if (result != null) {
            ScanRecord scanRecord = result.getScanRecord();
            if (scanRecord == null) {
                return null;
            }
            ScanData data = new ScanData(result.getDevice(), result.getRssi(),
                    scanRecord.getBytes());
            return data;
        }
        return null;
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        super.onBatchScanResults(results);
        for (ScanResult result : results) {
            ScanData data = parseScanResult(result);
            if(mOnBlueScanListener != null && data != null){
                mOnBlueScanListener.onBlueScan(data);
            }
        }
    }

    @Override
    public void onScanFailed(int errorCode) {
        super.onScanFailed(errorCode);
    }
}

