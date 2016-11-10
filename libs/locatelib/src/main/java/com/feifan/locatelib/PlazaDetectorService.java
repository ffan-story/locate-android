package com.feifan.locatelib;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;

import com.feifan.baselib.utils.IOUtils;
import com.feifan.baselib.utils.LogUtils;
import com.feifan.indoorlocation.model.BeaconDB;
import com.feifan.indoorlocation.model.IndoorLocationInfoModel;
import com.feifan.locatelib.cache.PlazaBeaconInfo;
import com.feifan.locatelib.cache.PlazaBeaconInfo.BeaconInfo;
import com.feifan.locatelib.cache.PlazaBeaconInfo.PlazaInfo;
import com.feifan.locatelib.cache.PlazaFingerprintInfo;
import com.feifan.locatelib.cache.RxPlazaDataService;
import com.feifan.locatelib.data.BeaconStore;
import com.feifan.locatelib.network.HttpResult;
import com.feifan.locatelib.network.HttpResultSubscriber;
import com.feifan.locatelib.network.ServiceFactory;
import com.feifan.scanlib.beacon.RawBeacon;
import com.feifan.scanlib.scanner.CycledLeScanCallback;
import com.feifan.scanlib.scanner.CycledLeScanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import rx.Observable;
import rx.functions.Func1;


/**
 * Created by xuchunlei on 2016/11/8.
 */

public class PlazaDetectorService extends IntentService implements CycledLeScanCallback {

    public static final int RESULT_CODE_PLAZA = 1;

    public static final String RESULT_KEY_PLAZA = "plaza";
    public static final String RESULT_KEY_PLAZA_FLOOR = "plaza_floor";

    private static final Set<String> UUID_STORE;

    static {
        UUID_STORE = new HashSet<>();
    }

    // scan
    private CycledLeScanner scanner;
    private List<RawBeacon> mData = new ArrayList<>();

    // synchronized
    private Semaphore mutex = new Semaphore(1);

    public PlazaDetectorService() {
        super("PlazaDetectorService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化
        if (UUID_STORE.isEmpty()) {
            InputStream is = null;
            InputStreamReader reader = null;
            BufferedReader bufferedReader = null;

            try {
                is = getAssets().open("uuid.store");
                reader = new InputStreamReader(is);
                bufferedReader = new BufferedReader(reader);
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    UUID_STORE.add(str);
                }
                LogUtils.i("load " + UUID_STORE.size() + " uuid for filter");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(bufferedReader);
                IOUtils.closeQuietly(reader);
                IOUtils.closeQuietly(is);
            }

            ServiceFactory.getInstance().initialize(this);
        }

        // 在主线程中开启扫描
        acquire(mutex);
        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = manager.getAdapter();
        scanner = CycledLeScanner.createScanner(adapter, this);
        scanner.startAtInterval(3000);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final ResultReceiver receiver = intent.getParcelableExtra("receiver");

        // 在工作线程中获取广场信息
        acquire(mutex);
        LogUtils.i("query plaza info via beacon data");

        // 统一使用数组处理数据
        final RawBeacon[] dataArray = mData.toArray(new RawBeacon[0]);

        // 找到出现频率最多的beacon
        RawBeacon beacon = findBestBeacon(dataArray);


        // todo 通过本地缓存定位广场

        // todo 通过网路缓存定位广场

        final RxPlazaDataService plazaService = ServiceFactory.getInstance().createService(RxPlazaDataService.class, "http://10.1.82.142:8081/");

        // test
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("callId", "0");
        paramMap.put("signValue", "1");


//        plazaService.getPlazaBaseInfo(beacon.uuid, beacon.major, beacon.minor, paramMap)
        plazaService.getPlazaBaseInfo("A3FCE438-627C-42B7-AB72-DC6E55E137AC", 11000, 43102, paramMap)
                .onErrorReturn(new Func1<Throwable, HttpResult<PlazaBeaconInfo>>() {
                    @Override
                    public HttpResult<PlazaBeaconInfo> call(Throwable throwable) {
                        LogUtils.e("request plaza base info falied for " + throwable.getMessage());
                        return null;
                    }
                })
                .flatMap(new Func1<HttpResult<PlazaBeaconInfo>, Observable<HttpResult<PlazaFingerprintInfo>>>() {
                    @Override
                    public Observable<HttpResult<PlazaFingerprintInfo>> call(HttpResult<PlazaBeaconInfo> plazaBeaconInfoHttpResult) {

                        // 初始化缓存 TODO 放到Native层
                        if(plazaBeaconInfoHttpResult != null) {
                            assert plazaBeaconInfoHttpResult.data == null;
                            assert plazaBeaconInfoHttpResult.data.beacons == null;
                            assert plazaBeaconInfoHttpResult.data.plaza == null;

                            final List<PlazaBeaconInfo.BeaconInfo> beacons = plazaBeaconInfoHttpResult.data.beacons;
                            BeaconStore.getInstance().initialize(beacons);
                            // 寻找楼层
//                        Set<RawBeacon> top3Beacons = findTopByRssi(dataArray, 3);
//                        int floor = BeaconStore.getInstance().selectFloor(top3Beacons);

                            // temp code
                            int floor = 2;

                            // 返回结果
                            Bundle args = new Bundle();
                            args.putParcelable(RESULT_KEY_PLAZA, plazaBeaconInfoHttpResult.data.plaza);
                            args.putInt(RESULT_KEY_PLAZA_FLOOR, floor);
                            receiver.send(RESULT_CODE_PLAZA, args);

                        }

                        // temp code
//                        Bundle args = new Bundle();
//                        args.putParcelable(RESULT_KEY_PLAZA, );
//                        args.putInt(RESULT_KEY_PLAZA_FLOOR, 31);
//                        receiver.send(RESULT_CODE_PLAZA, args);

                        return plazaService.getPlazaFingerprint();
                    }
                })
                .subscribe(new HttpResultSubscriber<PlazaFingerprintInfo>() {
                    @Override
                    protected void _onError(Throwable e) {

                    }

                    @Override
                    protected void _onSuccess(PlazaFingerprintInfo data) {
                        // todo 处理指纹数据
                    }
                });

        // temp
        IndoorLocationInfoModel model = new IndoorLocationInfoModel();
        model.plazaId = "android_860100010030500015";
        model.plazaName = "石景山万达广场";

        // 通知调用方
        BeaconDB db = new BeaconDB(model, null);
        LocatorFactory.getDefaultLocator().setBeaconDB(db);

    }

    @Override
    public void onCycleBegin() {
        LogUtils.i("start scanning beacon data for query plaza info");
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        RawBeacon beacon = RawBeacon.fromScanData(device, rssi, scanRecord);
        if (UUID_STORE.contains(beacon.uuid)) {
            mData.add(beacon);
            LogUtils.d("we got " + mData.size() + " valid beacon data now");
        }
    }

    @Override
    public void onCycleEnd() {
        scanner.stop();
        LogUtils.i("stop scanning beacon data for query plaza info");
        release(mutex);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e("PlazaDetector is destroyed");
    }

    private void acquire(Semaphore mutex) {
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void release(Semaphore mutex) {
        mutex.release();
    }

    private Set<RawBeacon> findTopByRssi(RawBeacon[] data, int k) {
        Arrays.sort(data, new Comparator<RawBeacon>() {
            @Override
            public int compare(RawBeacon r1, RawBeacon r2) {
                return r2.rssi - r1.rssi;
            }
        });

        Set<RawBeacon> result = new LinkedHashSet<>(3);

        for(RawBeacon beacon : data) {
            if(result.size() != k && !result.contains(beacon)) {
                result.add(beacon);
            }
        }
        return result;
    }

    private RawBeacon findBestBeacon(RawBeacon[] data) {
        if (data.length == 0) {
            return null;
        }

        Map<RawBeacon, Integer> statMap = new HashMap<>();
        RawBeacon bestBeacon = data[0];
        int bestCount = 1;
        for (RawBeacon beacon : data) {
            int count = 1;
            if (statMap.containsKey(beacon)) {
                count = statMap.get(beacon) + 1;

            }
            statMap.put(beacon, count);
            if (count > bestCount) {
                bestCount = count;
                bestBeacon = beacon;
            }
        }
        LogUtils.d("find best beacon, " + bestBeacon.toString() + "(" + bestCount + ")");
        return bestBeacon;
    }

}
