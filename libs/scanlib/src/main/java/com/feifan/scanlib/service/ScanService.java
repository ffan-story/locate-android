package com.feifan.scanlib.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.scanlib.ScanManager;
import com.feifan.scanlib.beacon.BeaconData;
import com.feifan.scanlib.beacon.RawBeacon;
import com.feifan.scanlib.scanner.CycledLeScanCallback;
import com.feifan.scanlib.scanner.CycledLeScanner;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 扫描服务
 * <pre>
 *     为采样提供数据扫描服务，目前支持：
 *     （1）Beacon数据扫描
 * </pre>
 * Created by xuchunlei on 16/9/1.
 */
public class ScanService extends Service {

    private CycledLeScanner mScanner;
    private DataCallback mCallback;
    private ScanState mState = new ScanState();
    // 处理蓝牙数据的线程池
    private ExecutorService mExecutor;

    @Override
    public void onCreate() {
        super.onCreate();
        BluetoothManager manager = (BluetoothManager)getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = manager.getAdapter();
        mScanner = CycledLeScanner.createScanner(adapter, mScanCallback);
        mExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    public void startScanBeacon(int period, DataCallback dataCallback) {
        mCallback = dataCallback;
        mScanner.startAtInterval(period);
    }

    public void stopScanBeacon() {
        mScanner.stop();
    }

    /*---------数据---------*/

    private final CycledLeScanCallback mScanCallback = new CycledLeScanCallback() {
        @Override
        public void onCycleBegin() {
//            LogUtils.e("time---------->" + System.currentTimeMillis());
        }

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            mExecutor.execute(new ScanProcessor(new ScanData(device, rssi, scanRecord)));
        }

        @Override
        public void onCycleEnd() {
            synchronized (mState) {
                mCallback.call(ScanService.this, new BeaconData(mState.finalizeBeacons()));
            }
        }
    };

    /**
     * 扫描数据
     */
    private static class ScanData{
        public ScanData(BluetoothDevice device, int rssi, byte[] scanRecord) {
            this.device = device;
            this.rssi = rssi;
            this.scanRecord = scanRecord;
        }

        int rssi;
        BluetoothDevice device;
        byte[] scanRecord;
    }

    /**
     * 扫描数据处理线程
     */
    private class ScanProcessor implements Runnable {

        private ScanData mData;

        public ScanProcessor(ScanData data) {
            mData = data;
        }

        @Override
        public void run() {
            RawBeacon beacon = RawBeacon.fromScanData(mData.device, mData.rssi, mData.scanRecord);
            if (beacon != null) {
                processBeaconFromScan(beacon);
            }
        }
    }

    private void processBeaconFromScan(RawBeacon beacon) {
        synchronized (mState) {
            mState.addBeacon(beacon);
        }
    }


    /*---------END---------*/


    /*-----------------进程间通讯---------------*/

    /**
     * Command to the service to display a message
     */
    public static final int MSG_START_SCAN = 1;
    public static final int MSG_STOP_SCAN = 2;

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler(this));

    static class IncomingHandler extends Handler {
        private final WeakReference<ScanService> mService;
        private DataCallback mCallback = null;

        IncomingHandler(ScanService service) {
            mService = new WeakReference<ScanService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            ScanService service = mService.get();
            msg.getData().setClassLoader(CommandData.class.getClassLoader());
            CommandData command = msg.getData().getParcelable("data");

            if (service != null) {
                switch (msg.what) {
                    case MSG_START_SCAN:
                        LogUtils.i("start scan received, scan will occur at every " + command.getPeriod());
                        if(mCallback == null) {
                            mCallback = new DataCallback(command.getCallPackageName());
                        }
                        service.startScanBeacon(command.getPeriod(), mCallback);
                        break;
                    case MSG_STOP_SCAN:
                        LogUtils.i("stop scan received, scan will be ended.");
                        service.stopScanBeacon();
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        }
    }

    /*--------------END---------------*/
}
