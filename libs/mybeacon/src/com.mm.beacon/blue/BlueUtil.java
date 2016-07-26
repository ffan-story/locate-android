package com.mm.beacon.blue;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.mm.beacon.exception.BleNotAvailableException;


/**
 * Created by mengmeng on 15/8/21.
 */
public class BlueUtil {
    /**
     * Check if Bluetooth LE is supported by this Android device, and if so, make sure it is enabled. Throws a
     * RuntimeException if Bluetooth LE is not supported. (Note: The Android emulator will do this)
     *
     * @return false if it is supported and not enabled
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean isSupportBlue(Context context) {
        if (context == null) {
            return false;
        }
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            throw new BleNotAvailableException("Bluetooth LE not supported by this device");
        } else if (context.getSystemService(Context.BLUETOOTH_SERVICE) != null
                && ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter() != null
                && ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter().isEnabled()) {
            return true;
        }

        return false;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean isSupportPeripheral(Context context) {
        if(isSupportBlue(context)) {
            final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter == null) {
                return false;
            }
            BluetoothLeAdvertiser bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
            if (bluetoothLeAdvertiser == null || !bluetoothAdapter.isMultipleAdvertisementSupported()) {
                return false;
            }
            return true;
        }
        return false;
    }
}
