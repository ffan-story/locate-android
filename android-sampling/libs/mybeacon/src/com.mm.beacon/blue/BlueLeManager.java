package com.mm.beacon.blue;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;

/**
 * Created by mengmeng on 15/8/21.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BlueLeManager extends IBlueManager implements BluetoothAdapter.LeScanCallback {
  private BluetoothAdapter mBluetoothAdapter;
  private static BlueLeManager mBlueManager;
  private BlueLeManager(Context context) {
    super(context);
    initBlue();
  }

  public static synchronized BlueLeManager getInstance(Context context) {
    if (mBlueManager == null) {
      mBlueManager = new BlueLeManager(context);
    }
    return mBlueManager;
  }

  private void initBlue() {
    final BluetoothManager bluetoothManager =
        (BluetoothManager) mContext.getApplicationContext().getSystemService(
            Context.BLUETOOTH_SERVICE);
    mBluetoothAdapter = bluetoothManager.getAdapter();
  }

  @Override
  public void startScan() {
    if(mBluetoothAdapter != null) {
      mBluetoothAdapter.startLeScan(this);
    }
  }

  @Override
  public void stopScan() {
    if(mBluetoothAdapter != null) {
      mBluetoothAdapter.stopLeScan(this);
    }
  }

  /**
   * Callback reporting an LE device found during a device scan initiated
   * by the {@link BluetoothAdapter#startLeScan} function.
   *
   * @param device Identifies the remote device
   * @param rssi The RSSI value for the remote device as reported by the
   *          Bluetooth hardware. 0 if no RSSI value is available.
   * @param scanRecord The content of the advertisement record offered by
   */
  @Override
  public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
    ScanData scanData = new ScanData(device, rssi, scanRecord);
    notifyScanListener(scanData);
  }
}
