package com.feifan.scanlib;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.scanlib.scanner.beacon.BeaconScanner;
import com.feifan.scanlib.scanner.beacon.BeaconScanner.BeaconData;
import com.feifan.scanlib.scanner.beacon.BeaconScanner.OnBeaconCallback;

/**
 * 扫描服务
 * <pre>
 *     为采样提供数据扫描服务，目前支持：
 *     （1）Beacon数据扫描
 * </pre>
 * Created by xuchunlei on 16/9/1.
 */
public class ScanService extends Service implements OnBeaconCallback {

    private BeaconScanner mScanner;

    @Override
    public void onCreate() {
        super.onCreate();
        BluetoothManager manager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mScanner = new BeaconScanner(manager.getAdapter());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IScanService.Stub mBinder = new IScanService.Stub() {

        @Override
        public void startScan(float x, float y, float initRadian) throws RemoteException {
            LogUtils.e("start scan at (" + x + "," + y +"), initRadian-->" + initRadian);
            mScanner.start(ScanService.this);
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    };

    @Override
    public void onBeaconScan(BeaconData data) {
        LogUtils.e(data.toString());
    }
}
