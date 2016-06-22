package com.mm.beacon.blue;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.os.Build;

/**
 * Created by mengmeng on 15/8/21.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BlueLOLLIPOPManager extends  IBlueManager implements  IBlueManager.OnBlueScanListener {
    private static BlueLOLLIPOPManager mBlueManager;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private  SyncScanCallback mSyncScanCallback;

    private BlueLOLLIPOPManager(Context context) {
        super(context);
        initBlue();
    }

    public static synchronized BlueLOLLIPOPManager getInstance(Context context) {
        if (mBlueManager == null) {
            mBlueManager = new BlueLOLLIPOPManager(context);
        }
        return mBlueManager;
    }

    private void initBlue() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) mContext.getApplicationContext().getSystemService(
                        Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mSyncScanCallback = new  SyncScanCallback(this);
    }

    public void startScan() {
        if (mBluetoothAdapter != null) {
            mBluetoothLeScanner.startScan(mSyncScanCallback);
        }
    }

    public void stopScan() {
        if (mBluetoothAdapter != null) {
            mBluetoothLeScanner.stopScan(mSyncScanCallback);
        }
    }

    @Override
    public void onBlueScan( ScanData scanData) {
        notifyScanListener(scanData);
    }
}
