package com.feifan.locatelib;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.feifan.baselib.utils.IOUtils;
import com.feifan.baselib.utils.LogUtils;
import com.feifan.locatelib.algorithm.LocationFinderFactory;
import com.feifan.locatelib.cache.CacheState;
import com.feifan.locatelib.cache.FingerprintStore;
import com.feifan.locatelib.cache.model.DownloadInfo;
import com.feifan.locatelib.cache.model.PlazaBeaconInfo;
import com.feifan.locatelib.cache.request.RxPlazaDataService;
import com.feifan.locatelib.cache.BeaconStore;
import com.feifan.locatelib.cache.request.RxPlazaFingerprintService;
import com.feifan.locatelib.network.HttpResult;
import com.feifan.locatelib.network.HttpResultSubscriber;
import com.feifan.locatelib.network.ServiceFactory;
import com.feifan.locatelib.network.UrlUtils;
import com.feifan.locatelib.utils.PrintUtils;
import com.feifan.scanlib.beacon.RawBeacon;
import com.feifan.scanlib.beacon.SampleBeacon;
import com.feifan.scanlib.scanner.CycledLeScanCallback;
import com.feifan.scanlib.scanner.CycledLeScanner;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import okhttp3.ResponseBody;
import okhttp3.internal.framed.Http2;
import okhttp3.internal.http.HttpMethod;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;


/**
 * Created by xuchunlei on 2016/11/8.
 */

public class PlazaDetectorService extends IntentService implements CycledLeScanCallback {

    /** 返回码-广场信息 */
    public static final int RESULT_CODE_PLAZA = 1;
    /** 返回码-状态 */
    public static final int RESULT_CODE_STATUS = 2;

    /** 状态码-成功 */
    public static final int STATUS_SUCCESS = 200;
    /** 状态码-指纹文件未找到 */
    public static final int STATUS_ERROR_FPFILE_NOT_FOUND = 2;
    /** 状态码-广场未找到 */
    public static final int STATUS_ERROR_PLAZA_NOT_FOUND = 3;
    /** 状态码-beacon点位文件未找到 */
    public static final int STATUS_ERROR_BEACON_FILE_NOT_FOUND = 4;
    /** 状态码-未知错误 */
    public static final int STATUS_ERROR_UNKNOWN = 5;

    // 状态码
    private int mStatus = -1;


    public static final String RESULT_KEY_PLAZA = "plaza";
    public static final String RESULT_KEY_PLAZA_FLOOR = "plaza_floor";
    public static final String RESULT_KEY_STATUS_FLAG = "status";
    public static final String RESULT_KEY_BEACON_FILE = "beacon_file";

    private static final Set<String> LOCATE_KEY; // SDK支持的所有UUID集合

    static {
        LOCATE_KEY = new HashSet<>();
    }

    // scan
    private CycledLeScanner scanner;
    private List<RawBeacon> mData = new ArrayList<>();
    private static final int MAX_SCAN_TIMES = 5;
    private int scanCount = 0;

    // synchronized
    private Semaphore mutex = new Semaphore(1);

    // data
    private RxPlazaDataService plazaService;
    private File fingerPrintDir;

    // query
    private boolean changed = false;
    private Map<String, String> paramMap = new HashMap<>();

    public PlazaDetectorService() {
        super("PlazaDetectorService");

        plazaService = ServiceFactory.getInstance()
                .createService(RxPlazaDataService.class, "https://api.ffan.com/");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 创建本地指纹目录
        fingerPrintDir = new File(getExternalCacheDir(), "fingerprints");
        if(!fingerPrintDir.exists()) {
            fingerPrintDir.mkdirs();
        }

        // 初始化
        if (LOCATE_KEY.isEmpty()) {
            InputStream is = null;
            InputStreamReader reader = null;
            BufferedReader bufferedReader = null;

            try {
                // 获取合法的定位键信息
                is = getAssets().open("locate.key");
                reader = new InputStreamReader(is);
                bufferedReader = new BufferedReader(reader);
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    LOCATE_KEY.add(str);
                }
                LogUtils.i("load " + LOCATE_KEY.size() + " uuid for filter");
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
        scanner.startAtInterval(1000);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final ResultReceiver receiver = intent.getParcelableExtra("receiver");

        // 在工作线程中获取广场信息
        acquire(mutex);
        LogUtils.i("query plaza info via beacon data");

        // 更新缓存状态
        mStatus = updateCacheState(mData);

        Bundle args = new Bundle();
        // 通知初始化广场信息
        if(CacheState.getInstance().isValid()) {

            args.putString(RESULT_KEY_PLAZA, CacheState.getInstance().getPlazaId());
            args.putInt(RESULT_KEY_PLAZA_FLOOR, CacheState.getInstance().getFloor());
            receiver.send(RESULT_CODE_PLAZA, args);

            mStatus = updateCacheData();
            LogUtils.i("detect plaza and floor successfully");
        }

        // 通知定位或错误状态
        args.clear();
        String beaconFile = getBeaconFileName();
        mStatus = beaconFile.length() == 0 ? STATUS_ERROR_BEACON_FILE_NOT_FOUND : mStatus;
        args.putInt(RESULT_KEY_STATUS_FLAG, mStatus);
        args.putString(RESULT_KEY_BEACON_FILE, getBeaconFileName());
        receiver.send(RESULT_CODE_STATUS, args);

    }

    @Override
    public void onCycleBegin() {
        LogUtils.i("start scanning beacon data for query plaza info");
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        RawBeacon beacon = RawBeacon.fromScanData(device, (byte)rssi, scanRecord);
        if (LOCATE_KEY.contains(beacon.uuid)) {
            mData.add(beacon);
            LogUtils.d("we got " + mData.size() + " valid beacon data now");
        }
    }

    @Override
    public void onCycleEnd() {
        if(mData.isEmpty()) {
            if(scanCount < MAX_SCAN_TIMES) {
                scanCount++;
                LogUtils.w("receive none valid data, try " + scanCount + " times");
                return;
            }
        }

        // 扫描到数据或超过最大尝试次数
        if(!mData.isEmpty() || scanCount >= MAX_SCAN_TIMES) {
            scanner.stop();
            release(mutex);
            LogUtils.i("stop scanning beacon data for query plaza info");
            if(mData.isEmpty()) {
                Toast.makeText(getApplicationContext(), "the plaza is not supported now", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e("PlazaDetector is destroyed");
    }

    /*-------------同步-------------*/
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

    /*-------------数据--------------*/

    // 返回值声明用来明确该方法具有状态，勿重构删除
    private int updateCacheState(final List<RawBeacon> data) {

        // 去除重复的beacon
        String[][] result = distinctBeacons(data);

        // 请求
        paramMap.clear();
        paramMap.put("umlist", UrlUtils.encodeToBase64(new Gson().toJson(result)));
        // 石景山万达
//        paramMap.put("umlist", UrlUtils.encodeToBase64("[[\"A3FCE438-627C-42B7-AB72-DC6E55E137AC\",\"11000\", \"42493\"]]"));
        // 爱琴海
//        paramMap.put("umlist", UrlUtils.encodeToBase64("[[\"A3FCE438-627C-42B7-AB72-DC6E55E137AC\",\"21001\"]]"));

        paramMap.put("reqTime", String.valueOf(System.currentTimeMillis()));
        paramMap.put("callId", "SYTM");
        final String singValue = UrlUtils.computeSignValue(paramMap);
        paramMap.put("signValue", singValue);

        plazaService.getPlazaBaseInfo(paramMap)
                .onErrorReturn(new Func1<Throwable, HttpResult<PlazaBeaconInfo>>() {
                    @Override
                    public HttpResult<PlazaBeaconInfo> call(Throwable throwable) {
                        LogUtils.e("request plaza base info falied for " + throwable.getMessage());
                        updateStatus(STATUS_ERROR_PLAZA_NOT_FOUND);
                        return null;
                    }
                })
                .flatMap(new Func1<HttpResult<PlazaBeaconInfo>, Observable<HttpResult<DownloadInfo>>>() {
                    @Override
                    public Observable<HttpResult<DownloadInfo>> call(HttpResult<PlazaBeaconInfo> result) {

                        paramMap.clear();
                        RxPlazaFingerprintService fpService = ServiceFactory.getInstance()
                                .createService(RxPlazaFingerprintService.class,
                                        "https://api.ffan.com/ihos/beacon/v1/");
                        // 初始化缓存 TODO 考虑放到Native层
                        if (result != null) {
                            if (result.status == STATUS_SUCCESS) {
                                final String plazaId = result.data.plazaId;
                                changed = isNewPlaza(plazaId);
                                LogUtils.d("plaza changed = " + changed + ", " + plazaId);
                                if (changed) { // 切换广场
                                    BeaconStore.getInstance().initialize(result.data.beacons); // 初始化beacon点位
                                    CacheState.getInstance().setPlazaId(plazaId);
                                    CacheState.getInstance().setVersion(getVersion(plazaId)); // 指纹库版本
                                } else {
                                    LogUtils.d("Cache state is ok, use it directly");
                                }

                                // 确定楼层
                                int floor = BeaconStore.getInstance().selectFloor(data);
                                CacheState.getInstance().setFloor(floor);
//                                CacheState.getInstance().setFloor(1);

                                // 请求指纹库
                                paramMap.clear();
                                paramMap.put("callId", "SYTM");
                                paramMap.put("plazaId", plazaId);
                                paramMap.put("reqTime", String.valueOf(System.currentTimeMillis()));
                                paramMap.put("version", CacheState.getInstance().getVersion());
                                String sign = UrlUtils.computeSignValue(paramMap);
                                paramMap.put("signValue", sign);
                            }else {
                                updateStatus(result.status);
                            }
                        }
                        // 使用本地缓存中的广场信息
                        return fpService.queryFingerprintFile(paramMap);
                    }
                })
                .subscribe(new Subscriber<HttpResult<DownloadInfo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        CacheState.getInstance().reset();
                        LogUtils.e("detect plaza and floor failed");
                    }

                    @Override
                    public void onNext(HttpResult<DownloadInfo> result) {
                        String url = result.data.down_url;
                        if(result.data.isLatest == 0) { // 有新版本，需要更新版本
                            int lastPath = url.lastIndexOf("/");
                            String key = url.substring(lastPath + 1, url.length());
                            CacheState.getInstance().setDownloadKey(key);
                            CacheState.getInstance().setVersion(result.data.version);
                        }else {
                            LogUtils.i("use the latest version " + CacheState.getInstance().getVersion() + " fingerprints");
                        }
                    }
                });
        return mStatus;
    }

    private int updateCacheData() {

        String downKey = CacheState.getInstance().getDownloadKey();
        LogUtils.d("downKey is " + downKey);
        final File f = new File(fingerPrintDir, CacheState.getInstance().getStoreName().concat(".zip"));
        if(!downKey.isEmpty()) {
            plazaService.downloadFPFile(downKey)
                        .flatMap(new Func1<Response<ResponseBody>, Observable<File>>() {
                            @Override
                            public Observable<File> call(Response<ResponseBody> response) {
                                // 下载指纹文件
                                try {
                                    BufferedSink sink = Okio.buffer(Okio.sink(f));
                                    sink.writeAll(response.body().source());
                                    sink.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return Observable.create(new Observable.OnSubscribe<File>() {
                                    @Override
                                    public void call(Subscriber<? super File> subscriber) {
                                        if(!f.exists()) {
                                            subscriber.onError(new IllegalStateException(f.getAbsolutePath() + " not found"));
                                        }
                                        subscriber.onNext(f);
                                        subscriber.onCompleted();
                                    }
                                });
                            }
                        })
                        .subscribe(new Subscriber<File>() {
                            @Override
                            public void onCompleted() {
                                updateStatus(STATUS_SUCCESS);
                            }

                            @Override
                            public void onError(Throwable e) {
                                updateStatus(STATUS_ERROR_UNKNOWN);
                            }

                            @Override
                            public void onNext(File file) {
                                if (changed) {
                                    FingerprintStore.getInstance().initialize(file);
                                    FingerprintStore.getInstance().serialize();
                                }
                                LogUtils.i("save fingerprint file to " + file.getAbsolutePath());
                            }
                        });
        } else {
            if(f.exists()) {
                FingerprintStore.getInstance().initialize(f);
                updateStatus(STATUS_SUCCESS);
            }else {
                updateStatus(STATUS_ERROR_FPFILE_NOT_FOUND);
            }
        }
        return mStatus;
    }

    private String[][] distinctBeacons(List<RawBeacon> data) {
        Set<String> resultKey = new HashSet<>();
        for(RawBeacon beacon : data) {
            String key = beacon.uuid + "," + beacon.major + "," + beacon.minor;
            if(!resultKey.contains(key)) {
                resultKey.add(key);
            }
        }
        String[][] array = new String[resultKey.size()][];
        int i = 0;
        for(String key : resultKey) {
            String[] keyArray = key.split(",");
            array[i] = keyArray;
            i++;
        }
        return array;
    }

    private boolean isNewPlaza(String id) {
        if(CacheState.getInstance().getPlazaId() == null) {
            return true;
        }
        return !CacheState.getInstance().getPlazaId().equals(id);
    }

    private String getVersion(final String plazeId) {
        String version = "0";
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".store")
                        && name.startsWith(plazeId);
            }
        };

        String[] stores = fingerPrintDir.list(filter);
        if(stores.length > 1) { // 指纹数据异常
            for(String name : stores) {
                deleteFile(new File(fingerPrintDir, name));
            }
        } else if(stores.length == 0) {
            LogUtils.d(plazeId + "'s store file not found");
        } else if(stores.length == 1) {
            int beginIndex = stores[0].indexOf("v");
            int endIndex = stores[0].indexOf(".");
            if(beginIndex != -1) { // 文件名合法
                version = stores[0].substring(beginIndex + 1, endIndex);
            } else {
                deleteFile(new File(fingerPrintDir, stores[0]));
            }

        }
        return version;
    }

    // 保持第一个错误状态
    private void updateStatus(int status) {
        if(mStatus == -1 || mStatus == STATUS_SUCCESS) {
            mStatus = status;
        }
        LogUtils.e("mStatus" + mStatus + ",status=" + status);
    }

    /**
     * 删除文件或目录
     * @param file
     */
    private void deleteFile(File file){
        if(file == null) {
            return;
        }
        if(file.isFile()){
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                file.delete();
                return;
            }
            for(File f : childFile){
                deleteFile(f);
            }
            file.delete();
        }
    }

    // 获取Beacon点位文件名称
    private String getBeaconFileName() {
        String jsonFileName = CacheState.getInstance().getStoreName()
                .concat(File.separator).concat(CacheState.getInstance().getPlazaId().concat(".json"));
        File jsonFile = new File(fingerPrintDir, jsonFileName);
        if(jsonFile.exists()) {
            return jsonFile.getAbsolutePath();
        }
        return "";
    }

//    private RawBeacon findBestBeacon(RawBeacon[] data) {
//        if (data.length == 0) {
//            return null;
//        }
//
//        Map<RawBeacon, Integer> statMap = new HashMap<>();
//        RawBeacon bestBeacon = data[0];
//        int bestCount = 1;
//        for (RawBeacon beacon : data) {
//            int count = 1;
//            if (statMap.containsKey(beacon)) {
//                count = statMap.get(beacon) + 1;
//
//            }
//            statMap.put(beacon, count);
//            if (count > bestCount) {
//                bestCount = count;
//                bestBeacon = beacon;
//            }
//        }
//        LogUtils.d("find best beacon, " + bestBeacon.toString() + "(" + bestCount + ")");
//        return bestBeacon;
//    }

    // temp
    public static List<SampleBeacon> SAMPLES_860100010060300001 = new ArrayList<>();
    static {
        // -1
//        SAMPLES_860100010060300001.add(new SampleBeacon(42529, -80)); // 31
//        SAMPLES_860100010060300001.add(new SampleBeacon(42594, -78)); // 81
//        SAMPLES_860100010060300001.add(new SampleBeacon(43503, -66));  // 573
//        SAMPLES_860100010060300001.add(new SampleBeacon(42841, -71)); // 192

        // locate 1
//         1,loc_x: 52.481, loc_y: -86.201
//        SAMPLES_860100010060300001.add(new SampleBeacon(42513, -91));
//        SAMPLES_860100010060300001.add(new SampleBeacon(42514, -79));
//        SAMPLES_860100010060300001.add(new SampleBeacon(42559, -89));
//        SAMPLES_860100010060300001.add(new SampleBeacon(42567, -78));
//        SAMPLES_860100010060300001.add(new SampleBeacon(42614, -90));
//        SAMPLES_860100010060300001.add(new SampleBeacon(42651, -85));
//        SAMPLES_860100010060300001.add(new SampleBeacon(42793, -86));
//        SAMPLES_860100010060300001.add(new SampleBeacon(42857, -69));
//        SAMPLES_860100010060300001.add(new SampleBeacon(42999, -90));
//        SAMPLES_860100010060300001.add(new SampleBeacon(43015, -85));
//        SAMPLES_860100010060300001.add(new SampleBeacon(43096, -75));
//        SAMPLES_860100010060300001.add(new SampleBeacon(43124, -84));
//        SAMPLES_860100010060300001.add(new SampleBeacon(43131, -88));
//        SAMPLES_860100010060300001.add(new SampleBeacon(43152, -73));
//        SAMPLES_860100010060300001.add(new SampleBeacon(43157, -76));
//        SAMPLES_860100010060300001.add(new SampleBeacon(43202, -90));
//        SAMPLES_860100010060300001.add(new SampleBeacon(43228, -89));
//        SAMPLES_860100010060300001.add(new SampleBeacon(43255, -89));
//        SAMPLES_860100010060300001.add(new SampleBeacon(43296, -79));
//        SAMPLES_860100010060300001.add(new SampleBeacon(43303, -82));
//        SAMPLES_860100010060300001.add(new SampleBeacon(43328, -90));
//        SAMPLES_860100010060300001.add(new SampleBeacon(43443, -91));
//        SAMPLES_860100010060300001.add(new SampleBeacon(43482, -83));
//        SAMPLES_860100010060300001.add(new SampleBeacon(43515, -86));


        // {'floor': 3, 'loc_x': 169.56, 'loc_y': -88.24}
//        SAMPLES_860100010060300001.add(new SampleBeacon(42545, -85));
//        SAMPLES_860100010060300001.add(new SampleBeacon(42647, -89));
//        SAMPLES_860100010060300001.add(new SampleBeacon(42653, -86));
//        SAMPLES_860100010060300001.add(new SampleBeacon(42670, -88));
//        SAMPLES_860100010060300001.add(new SampleBeacon(42804, -88));
//        SAMPLES_860100010060300001.add(new SampleBeacon(42809, -83));
//        SAMPLES_860100010060300001.add(new SampleBeacon(42848, -84));
//        SAMPLES_860100010060300001.add(new SampleBeacon(42853, -90));
//        SAMPLES_860100010060300001.add(new SampleBeacon(42864, -87));
//        SAMPLES_860100010060300001.add(new SampleBeacon(43194, -87));
//        SAMPLES_860100010060300001.add(new SampleBeacon(43253, -82));
//        SAMPLES_860100010060300001.add(new SampleBeacon(43358, -91));
//        SAMPLES_860100010060300001.add(new SampleBeacon(43387, -84));
//        SAMPLES_860100010060300001.add(new SampleBeacon(43389, -81));
//        SAMPLES_860100010060300001.add(new SampleBeacon(43415, -77));
//        SAMPLES_860100010060300001.add(new SampleBeacon(43447, -81));

        // {'floor': 1, 'loc_x': 198.42, 'loc_y': -61.91}
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42493, -76));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42496, -91));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42527, -84));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42539, -88));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42553, -69));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42593, -82));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42597, -70));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42608, -82));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42654, -77));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42658, -81));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42835, -89));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42853, -91));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42859, -88));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 42864, -80));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43097, -87));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43106, -77));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43182, -80));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43194, -84));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43201, -86));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43208, -89));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43213, -89));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43225, -65));

        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43289, -88));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43304, -80));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43357, -86));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43387, -90));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43395, -78));
        SAMPLES_860100010060300001.add(new SampleBeacon("a3fce438-627c-42b7-ab72-dc6e55e137ac", 11000, 43415, -86));

    }

}
