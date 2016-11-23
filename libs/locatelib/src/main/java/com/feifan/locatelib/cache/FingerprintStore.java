package com.feifan.locatelib.cache;

import android.os.SystemClock;

import com.feifan.baselib.utils.IOUtils;
import com.feifan.baselib.utils.LogUtils;
import com.feifan.scanlib.beacon.SampleBeacon;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 当前广场使用的指纹缓存
 * Created by xuchunlei on 2016/11/11.
 */

public class FingerprintStore {

    private static final FingerprintStore INSTANCE = new FingerprintStore();

    private int featureCount = 0;
    private byte[] featureCache;
    private byte[] featureSrc;
    private int[] resultCache;

    // file
    private String zipFileName;
    private String zipFilePath;
    private String zipPrefix;
    private File unZipPath;

    // data
    // 指纹数据，key为minor，value为minor对应的rssi和位置
//    private Map<Integer, List<ValueInfo>> values = new HashMap<>(12);
//    private Map<LocateInfo, Float> computeCache = new HashMap<>(12);
//    private Map<LocateInfo, Boolean> minorLocationMap = new HashMap<>();
    private Map<Integer, FPLocation[]>  fpMap = new HashMap<>();
    private FPLocation[] curStore;
    private Map<String, Integer> minorMap;

//    static class ValueInfo {
//        LocateInfo loc;
//        float rssi;
//
//        public ValueInfo(LocateInfo loc, float rssi) {
//            this.loc = loc;
//            this.rssi = rssi;
//        }
//    }

    public static class FPLocation {
        public float x;
        public float y;
        public int floor;
        FPFeature[] features;

        /**
         * 输入字符串格式形如：12.9833,-49.9981,-1
         * @param formatted
         * @return
         */
        public static FPLocation from(String formatted) {
            int xIndex = formatted.indexOf(",");
            int yIndex = formatted.lastIndexOf(",");
            FPLocation loc = new FPLocation();
            loc.x = Float.valueOf(formatted.substring(0, xIndex));
            loc.y = Float.valueOf(formatted.substring(xIndex + 1, yIndex));
            loc.floor = Integer.valueOf(formatted.substring(yIndex + 1, formatted.length()));
            return loc;
        }

        @Override
        public String toString() {
            String content = x + "," + y + "," + floor + ":";
            if(features != null && features.length != 0) {
                for(FPFeature feature : features) {
                    content += feature.toString() + ";";
                }
            }else {
                content += ";";
            }
            return content;
        }
    }

    /**
     * 特征类
     */
    public static class FPFeature {
        int index;
        byte rssi;

        /**
         * 输入字符串格式形如：36,-79
         * @param formatted
         * @return
         */
        public static FPFeature from(String formatted) {
            FPFeature feature = new FPFeature();
            int posIndex = formatted.indexOf(",");
            feature.index = Integer.valueOf(formatted.substring(0, posIndex));
            feature.rssi = Byte.valueOf(formatted.substring(posIndex + 1, formatted.length()));
            return feature;
        }

        @Override
        public String toString() {
            return index + "," + rssi;
        }
    }

    private FingerprintStore() {

    }

    public static FingerprintStore getInstance() {
        return INSTANCE;
    }

    public void initialize(File fingerprintFile, Map<String, Integer> minorMap) {
        if(fingerprintFile == null) {
            throw new NullPointerException("fingerprint file not found");
        }

        zipFileName = fingerprintFile.getName();
        zipFilePath = fingerprintFile.getParent().concat(File.separator);
        zipPrefix = getZipFileName(zipFileName);

        this.minorMap = minorMap;

//        for(int i = 0;i < minor_860100010030500015.length;i++) {
//            minorMap.put(minor_860100010030500015[i], i);
//        }

//        StoreUtils.initializeStore(minor_860100010060300001, minorMap, values);

        unZipPath = new File(ZipUtils.getDefaultUnZipDir(fingerprintFile.getAbsolutePath()));

        // 初始化缓冲

        featureCount = minorMap.size();
        featureCache = new byte[featureCount];

        featureSrc = new byte[featureCount];
        for(int i = 0;i < featureCount;i++) {
            featureSrc[i] = StoreUtils.NO_SIGNAL;
        }

        // 加载全部指纹库
        long begin = SystemClock.elapsedRealtime();
        loadAll(fingerprintFile.getAbsolutePath());
        LogUtils.i("load " + zipFileName + " consume " + (SystemClock.elapsedRealtime() - begin) + " ms");
    }

    /**
     * 载入指纹库
     * @param floor 表示指定楼层指纹
     */
    public void load(int floor) {
        if(fpMap.containsKey(floor)) {
            curStore = fpMap.get(floor);
        }else {
            String floorEntry = zipPrefix + "_" + floor;
            LogUtils.d("we try to find fingerprint file:" + floorEntry);
            String outName = unZipPath.getAbsolutePath().concat(File.separator).concat(floorEntry);

            ZipEntry entry;
            InputStream is = null;
            ZipInputStream zis = null;
            FileOutputStream fos = null;
            try {
                is = new FileInputStream(zipFilePath.concat(zipFileName));
                zis = new ZipInputStream(new BufferedInputStream(is));

                byte[] buffer = new byte[1024];
                while((entry = zis.getNextEntry()) != null) {
                    if(entry.getName().equals(floorEntry)) {
                        fos = new FileOutputStream(outName);
                        int count;
                        while ((count = zis.read(buffer)) != -1) {
                            fos.write(buffer, 0, count);
                        }
                        LogUtils.i("save fingerprint file to " + outName + " for " + floorEntry);
                        break;
                    }
                    zis.closeEntry();
                }
                zis.closeEntry();
//            // 加载指纹库
//            values = StoreUtils.fillStore(outName, minor_860100010060300001, values);
                curStore = StoreUtils.generateStore(outName);
                fpMap.put(floor, curStore);
//                printFingerprints(curStore);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(is);
                IOUtils.closeQuietly(zis);
                IOUtils.closeQuietly(fos);
            }
        }
    }

    private void loadAll(String zipFile) {
        File f = new File(getStoreFileName());
        LogUtils.e(f.getAbsolutePath());
        if(f.exists()) { // 使用压缩后的文件初始化指纹库
            fpMap.putAll(StoreUtils.generateStoreQuick(f.getAbsolutePath()));
        } else {
            List<String> outputFiles = ZipUtils.unZip(zipFile);
            for(String file : outputFiles) {
                FPLocation[] fps = StoreUtils.generateStore(file);
//                FPLocation[] fps = StoreUtils.generateStore1(file);
                fpMap.put(parseFloor(file), fps);
                LogUtils.i("generate fingerprint from " + file);
            }
        }
    }

    public void serialize() {
        Set<Map.Entry<Integer, FPLocation[]>> storeSet = fpMap.entrySet();
        String fileName = getStoreFileName();
        if(!exists(fileName)) {
            for(Map.Entry<Integer, FPLocation[]> fpLoc : storeSet) {
                StoreUtils.saveStore(fileName, fpLoc.getKey(), fpLoc.getValue());
                LogUtils.e("serialize data for floor " + fpLoc.getKey());
            }
            LogUtils.i("serialize fingerprint data to " + fileName);
        }else {
            LogUtils.i("fingerprint compress file exists, use it");
        }

    }

    public String getStoreFileName() {
        return zipFilePath.concat(getZipFileName(zipFileName)).concat(".store");
    }

    public FPLocation selectLocation(List<SampleBeacon> samples) {
        // 构建样本特征向量
        System.arraycopy(featureSrc, 0, featureCache, 0, featureCount);
        for(SampleBeacon beacon : samples) {
            featureCache[minorMap.get(beacon.toIdentityString())] = (byte) beacon.rssi;
        }
//        String test = "-99#-74#-99#-99#-78#-99#-99#-86#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-75#-99#-99#-99#-99#-99#-99#-89#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-89#-99#-99#-99#-99#-99#-99#-80#-99#-99#-99#-99#-99#-99#-99#-89#-99#-99#-99#-88#-99#-99#-99#-99#-89#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-90#-99#-99#-77#-99#-99#-99#-99#-99#-99#-99#-99#-99#-81#-78#-99#-99#-99#-99#-99#-99#-99#-99#-99#-88#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-83#-99#-99#-99#-84#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-91#-99#-99#-99#-99#-99#-87#-99#-99#-99#-99#-99#-99#-99#-83#-99#-99#-99#-99#-99#-99#-99#-99#-73#-99#-91#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-72#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-91#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-90#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-82#-99#-99#-99#-99#-99#-81#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-92#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-89#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-55#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-89#-99#-99#-99#-90#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-91#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-86#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-81#-99#-99#-99#-99#-99#-99#-99#-89#-99#-99#-99#-87#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-85#-99#-99#-99#-99#-90#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-87#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99#-99";
//        // #171.230239015_-87.2800332068_0_1.0
//        String[] testArray = test.split("#");
//        for(int i = 0;i < testArray.length;i++) {
//            featureCache[i] = Byte.parseByte(testArray[i]);
//        }

        FPLocation minLoc = null;
        int min = Integer.MAX_VALUE;
        for(FPLocation loc : curStore) {
            final FPFeature[] feasures = loc.features;
            int i;
            int currentPos = 0;
            int sum = 0;
            int sub;
            for(FPFeature feasure : feasures) {
                for(i = currentPos;i < feasure.index - 1;i++) {
                    // 计算非法特征值差平方之和
                    sub = featureCache[i] + 99;
                    sum += sub * sub;
                }
                // 计算合法特征项的差平方之和
                sub = featureCache[feasure.index] - feasure.rssi;
                sum += sub * sub;
                currentPos = feasure.index + 1;
            }

            for(i = currentPos;i < featureCount;i++) {
                sub = featureCache[i] + 99;
                sum += sub * sub;
            }
            if(sum < min) {
                min = sum;
                minLoc = loc;
//                LogUtils.e("min=" + min + "--->(" + minLoc.x + "," + minLoc.y + "," + minLoc.floor + ")");
            }
        }

        return minLoc;
    }

    private boolean exists(String fileName) {
        return new File(fileName).exists();
    }

    private int parseFloor(String fileName) {
        int prefixIndex = fileName.indexOf("_");
        if(prefixIndex == -1) {
            throw new IllegalArgumentException(fileName + " is invalid, '_' not found ");
        }
        String floorValue = fileName.substring(prefixIndex + 1, fileName.length());
        try {
            int floor = Integer.valueOf(floorValue);
            LogUtils.d("parse floor " + floor + " from " + fileName);
            return floor;
        }catch (NumberFormatException e) {
            e.printStackTrace();
        }

        throw new IllegalArgumentException(fileName + " is invalid, floor not found ");
    }

    private String getZipFileName(String name) {
        if ((name != null) && (name.length() > 0)) {
            int dot = name.lastIndexOf('.');
            if ((dot >-1) && (dot < (name.length()))) {
                return name.substring(0, dot);
            }
        }
        return name;
    }

    // test
    private void printFingerprints(FPLocation[] fps) {
                    int i = 0;
            for(FPLocation loc : fps) {
                LogUtils.i(i + "----->" + loc.x + "," + loc.y + "," + loc.features);
                String featureStr = "[";
                if(loc.features != null && loc.features.length != 0) {
                    for(FPFeature feasure : loc.features) {
                        featureStr += "(" + feasure.index + "," + feasure.rssi + "), ";
                    }
                    featureStr = featureStr.substring(0, featureStr.length() - 2) + "]";
                    LogUtils.i("        " + featureStr);
                }else {
                    LogUtils.e("error, no feature found on line " + i);
                }
                i++;
            }

    }

    // temp
    private static int[] minor_860100010060300001 = {
            41632, 41633, 42492, 42493, 42494, 42495, 42496, 42497, 42498, 42502, 42503, 42504, 42505, 42506, 42507, 42509,
            42511, 42512, 42513, 42514, 42515, 42516, 42517, 42519, 42521, 42522, 42523, 42525, 42526, 42527, 42528, 42529,
            42531, 42532, 42533, 42534, 42537, 42538, 42539, 42540, 42541, 42542, 42544, 42545, 42546, 42547, 42548, 42549,
            42551, 42552, 42553, 42554, 42555, 42557, 42558, 42559, 42560, 42561, 42562, 42563, 42564, 42565, 42566, 42567,
            42568, 42569, 42571, 42572, 42574, 42575, 42576, 42577, 42578, 42579, 42580, 42581, 42589, 42590, 42591, 42592,
            42593, 42594, 42595, 42596, 42597, 42598, 42602, 42606, 42607, 42608, 42609, 42614, 42615, 42616, 42617, 42618,
            42619, 42621, 42622, 42623, 42625, 42626, 42627, 42628, 42629, 42630, 42631, 42632, 42633, 42634, 42636, 42637,
            42638, 42640, 42641, 42642, 42643, 42644, 42645, 42646, 42647, 42648, 42649, 42650, 42651, 42652, 42653, 42654,
            42655, 42656, 42657, 42658, 42660, 42661, 42662, 42663, 42665, 42666, 42667, 42668, 42669, 42670, 42672, 42673,
            42675, 42676, 42678, 42679, 42681, 42788, 42789, 42790, 42791, 42792, 42793, 42794, 42795, 42798, 42800, 42801,
            42802, 42803, 42804, 42805, 42806, 42807, 42809, 42810, 42811, 42812, 42813, 42814, 42815, 42816, 42818, 42819,
            42820, 42822, 42823, 42824, 42825, 42826, 42828, 42829, 42830, 42831, 42833, 42834, 42835, 42836, 42837, 42839,
            42841, 42843, 42844, 42846, 42848, 42849, 42850, 42851, 42852, 42853, 42854, 42855, 42857, 42859, 42860, 42862,
            42864, 42865, 42866, 42867, 42868, 42870, 42871, 42872, 42873, 42874, 42875, 42876, 42877, 42878, 42879, 42880,
            42881, 42988, 42990, 42991, 42997, 42998, 42999, 43001, 43002, 43003, 43005, 43007, 43008, 43009, 43010, 43011,
            43013, 43014, 43015, 43016, 43017, 43018, 43020, 43021, 43022, 43024, 43025, 43026, 43027, 43029, 43030, 43033,
            43034, 43035, 43038, 43039, 43040, 43042, 43044, 43045, 43046, 43047, 43048, 43049, 43050, 43052, 43053, 43054,
            43055, 43057, 43058, 43059, 43060, 43061, 43062, 43064, 43065, 43066, 43068, 43069, 43071, 43072, 43073, 43074,
            43075, 43076, 43077, 43078, 43079, 43081, 43084, 43086, 43087, 43088, 43089, 43090, 43091, 43092, 43093, 43094,
            43095, 43096, 43097, 43098, 43099, 43100, 43101, 43102, 43103, 43104, 43105, 43106, 43107, 43108, 43109, 43110,
            43111, 43112, 43113, 43115, 43116, 43117, 43119, 43121, 43122, 43123, 43124, 43125, 43126, 43127, 43128, 43129,
            43130, 43131, 43132, 43134, 43135, 43136, 43137, 43138, 43141, 43142, 43143, 43145, 43146, 43147, 43148, 43149,
            43150, 43152, 43153, 43154, 43155, 43156, 43157, 43158, 43160, 43161, 43162, 43163, 43164, 43165, 43166, 43167,
            43171, 43172, 43173, 43174, 43175, 43176, 43177, 43178, 43179, 43180, 43181, 43182, 43183, 43184, 43185, 43186,
            43187, 43188, 43189, 43190, 43192, 43193, 43194, 43195, 43196, 43198, 43199, 43201, 43202, 43203, 43204, 43205,
            43206, 43208, 43209, 43210, 43213, 43214, 43215, 43216, 43217, 43220, 43221, 43222, 43223, 43224, 43225, 43226,
            43227, 43228, 43229, 43230, 43231, 43232, 43233, 43234, 43236, 43237, 43238, 43240, 43241, 43243, 43245, 43248,
            43249, 43250, 43251, 43253, 43254, 43255, 43258, 43259, 43260, 43262, 43263, 43264, 43265, 43267, 43268, 43270,
            43282, 43284, 43286, 43287, 43288, 43289, 43290, 43291, 43292, 43293, 43294, 43296, 43297, 43301, 43302, 43303,
            43304, 43305, 43307, 43309, 43310, 43311, 43312, 43313, 43314, 43315, 43316, 43317, 43318, 43319, 43320, 43321,
            43322, 43323, 43325, 43326, 43328, 43329, 43330, 43332, 43333, 43335, 43337, 43338, 43341, 43342, 43343, 43348,
            43349, 43350, 43352, 43354, 43355, 43356, 43357, 43358, 43359, 43361, 43382, 43383, 43385, 43386, 43387, 43388,
            43389, 43390, 43391, 43392, 43394, 43395, 43396, 43397, 43398, 43399, 43400, 43401, 43402, 43403, 43404, 43405,
            43406, 43407, 43409, 43410, 43411, 43412, 43414, 43415, 43416, 43417, 43419, 43423, 43425, 43426, 43427, 43428,
            43429, 43430, 43436, 43437, 43438, 43440, 43443, 43444, 43446, 43447, 43449, 43450, 43451, 43452, 43455, 43482,
            43483, 43484, 43486, 43489, 43490, 43491, 43492, 43493, 43495, 43496, 43498, 43500, 43501, 43503, 43504, 43505,
            43506, 43507, 43508, 43509, 43510, 43511, 43512, 43513, 43514, 43515, 43516, 43517, 43518, 43519, 43520

};

    private static int[] minor_860100010030500015 = new int[] {
            101,102,103,104,105,108,109,110,111,112,
            113,114,115,116,117,118,119,120,125,126,
            127,128,129,130,131,132,133,134,135,136,
            137,140,141,142,143,144,145,146

    };

    //    public LocateInfo selectLocation(List<SampleBeacon> samples) {
//        LocateInfo result = null;
//
//        float totalSum = 0;
//
//        for(SampleBeacon beacon : samples) {
//            int minor = beacon.minor;
//            float rssi = beacon.rssi;
//            List<ValueInfo> minorColumn = values.get(minor);
//
//            // 没有与minor对应的rssi数值
//            if(minorColumn.size() == 0) {
//                continue;
//            }
//            for(ValueInfo info : minorColumn) {
//                float compute = rssi - info.rssi;
//                Float sum = computeCache.get(info.loc);
//                // 计算缓存中的其他位置使用最大值累加
//
//                // 更新有实际数据的缓存
//                if(sum == null) {
//                    sum = totalSum + compute * compute; // 此时已经累积的和
//                }else {
//                    sum += compute * compute;
//                }
//                computeCache.put(info.loc, sum);
//                minorLocationMap.put(info.loc, true);
//            }
//
//            // 更新计算缓存其他项
//            for(Map.Entry<LocateInfo, Float> entry : computeCache.entrySet()) {
//
//                if(minorLocationMap.get(entry.getKey()) == null) {
//                    entry.setValue(entry.getValue() + rssi * rssi);
//                }
//            }
//            totalSum += rssi * rssi; // 补偿累积的和
//            minorLocationMap.clear();
//        }
//
//        float min = Float.MAX_VALUE;
//        for(Map.Entry<LocateInfo, Float> entry : computeCache.entrySet()) {
//            if(min > entry.getValue()) {
//                min = entry.getValue();
//                result = entry.getKey();
//            }
//        }
//        LogUtils.d("found the shortest location(" + result.x + "," + result.y + "," + result.floor + "), distance=" + min);
//        return result;
//    }

}
