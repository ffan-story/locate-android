package com.mm.beacon.advertise;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.util.Log;

import com.mm.beacon.blue.BlueUtil;


/**
 * Created by mengmeng on 16/1/11.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AdvertiseManager {
    private Context mContext;
    private static AdvertiseManager mAdvertiseManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    public static AdvertiseManager getAdvertiseInstance(Context context) {
        if (mAdvertiseManager == null) {
            synchronized (AdvertiseManager.class) {
                if (mAdvertiseManager == null) {
                    mAdvertiseManager = new AdvertiseManager(context);
                }
            }
        }
        return mAdvertiseManager;
    }

    private AdvertiseManager(Context context) {
        mContext = context;
        if (BlueUtil.isSupportPeripheral(context)) {
            BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
            mBluetoothLeAdvertiser =mBluetoothAdapter.getBluetoothLeAdvertiser();
        }
    }

    public AdvertiseData createAdvertiseData(String ... uuid) {
        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
        for (int i = 0; i < uuid.length; i++) {
            String id = uuid[i];
            if(!TextUtils.isEmpty(id)){
//                dataBuilder.addServiceUuid(ParcelUuid.fromString(id));
                byte[] serviceData = new byte[] {
                        (byte) 0xF0, 0x00, 0x02, 0x15 };
                Log.e("uuid",ParcelUuid.fromString(id).toString());
                dataBuilder.addServiceData(ParcelUuid.fromString(id),serviceData);
            }
        }
        AdvertiseData advertiseData = dataBuilder.build();
        if (advertiseData == null) {

        }
        return advertiseData;
    }

    public void startAdvertise(String ... uuid){
        mBluetoothLeAdvertiser.startAdvertising(createAdvSettings(true, 0), createAdvertiseData(uuid), mAdvertiseCallback);
    }
    /**
     * create AdvertiseSettings
     */
    public static AdvertiseSettings createAdvSettings(boolean connectable, int timeoutMillis) {
        AdvertiseSettings.Builder settingsbuilder = new AdvertiseSettings.Builder();
        settingsbuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        settingsbuilder.setConnectable(connectable);
        settingsbuilder.setTimeout(timeoutMillis);
        settingsbuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        AdvertiseSettings advertiseSettings = settingsbuilder.build();
        return advertiseSettings;
    }

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);

        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            if (errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE) {

            } else if (errorCode == ADVERTISE_FAILED_TOO_MANY_ADVERTISERS) {

            } else if (errorCode == ADVERTISE_FAILED_ALREADY_STARTED) {

            } else if (errorCode == ADVERTISE_FAILED_INTERNAL_ERROR) {

            } else if (errorCode == ADVERTISE_FAILED_FEATURE_UNSUPPORTED) {

            }
        }
    };

    public void stopAdvertise() {
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
            mBluetoothLeAdvertiser = null;
        }
    }
}
