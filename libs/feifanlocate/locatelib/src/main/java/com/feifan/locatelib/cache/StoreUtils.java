package com.feifan.locatelib.cache;

import android.os.SystemClock;

import com.feifan.baselib.utils.IOUtils;
import com.feifan.baselib.utils.LogUtils;
import com.feifan.locatelib.cache.FingerprintStore.FPFeature;
import com.feifan.locatelib.cache.FingerprintStore.FPLocation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by xuchunlei on 2016/11/11.
 */

public class StoreUtils {

    public static final byte NO_SIGNAL = -99;

    private StoreUtils() {

    }

//    public static FPLocation[] generateStore1(String file) {
//        InputStream is = null;
//        InputStreamReader reader = null;
//        BufferedReader bufferedReader = null;
//        try {
//            // 获取合法的uuid
//            is = new FileInputStream(file);
//            reader = new InputStreamReader(is);
//            bufferedReader = new BufferedReader(reader);
//            String lineStr;
//            while ((lineStr = bufferedReader.readLine()) != null) {
////                lineStr.split("#");
//            }
//        }catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.closeQuietly(bufferedReader);
//            IOUtils.closeQuietly(reader);
//            IOUtils.closeQuietly(is);
//        }
//        return new FPLocation[]{};
//    }

    public static FPLocation[] generateStore(String file) {
        InputStream is = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        List<FPLocation> resultGen = new LinkedList<>();

        try {
            // 获取合法的uuid
            is = new FileInputStream(file);
            reader = new InputStreamReader(is);
            bufferedReader = new BufferedReader(reader);
            String lineStr;
            byte value;
            long begin = SystemClock.elapsedRealtime();
            // debug
            int lineCount = 0;

            while ((lineStr = bufferedReader.readLine()) != null) {

                List<FPFeature> feasureList = new LinkedList<>(); // 用于按序保存某个位置有效特征
                int previous = 0;
                int currentPos;
                int currentColulmn = 0;
                while((currentPos = lineStr.indexOf('#', previous)) > -1) {
                    value = Float.valueOf(lineStr.substring(previous, currentPos)).byteValue();
                    if(value > NO_SIGNAL) { // 有效的rssi
                        FPFeature feasure = new FPFeature();
                        feasure.index = currentColulmn;
                        feasure.rssi = value;
                        feasureList.add(feasure);
                    }
                    currentColulmn ++;
                    previous = currentPos + 1;
                }

                // 位置信息
                // fixme 优化位置信息放在行首
                if(!feasureList.isEmpty()) { // 过滤掉没有指纹数据的位置
                    String locValue = lineStr.substring(previous, lineStr.length());
                    String[] splits = locValue.split("_");
                    FPLocation loc = new FPLocation();
                    loc.x = Float.valueOf(splits[0]);
                    loc.y = Float.valueOf(splits[1]);
                    loc.floor = Float.valueOf(splits[3]).intValue();
                    loc.features = feasureList.toArray(new FPFeature[]{});

                    resultGen.add(loc);
                }

//                LogUtils.i((++lineCount) + " fingerprints was done, location(" + loc.x + "," + loc.y + "," + loc.floor + ")");
            }
            LogUtils.d("consume time is " + (SystemClock.elapsedRealtime() - begin));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(bufferedReader);
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(is);
        }
        return resultGen.toArray(new FPLocation[]{});
    }

    public static Map<Integer, FPLocation[]> generateStoreQuick(String file) {
        LogUtils.d("generate fingerprint store with quick mode");
        Map<Integer, FPLocation[]> fpMap = new HashMap<>();
        InputStream is = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;

        try {
            // 获取合法的uuid
            is = new FileInputStream(file);
            reader = new InputStreamReader(is);
            bufferedReader = new BufferedReader(reader);
            String lineStr;
            List<FPLocation> locList = new ArrayList<>();
            long begin = SystemClock.elapsedRealtime();
            while ((lineStr = bufferedReader.readLine()) != null) {

                int locIndex = lineStr.indexOf(":");
                if(locIndex != -1) {
                    FPLocation loc = FPLocation.from(lineStr.substring(0, locIndex));
                    List<FPFeature> featureList = new LinkedList<>(); // 用于按序保存某个位置有效特征
                    int previous = locIndex + 1;
                    int currentPos;
                    while((currentPos = lineStr.indexOf(';', previous)) > -1) {

                        FPFeature feature = FPFeature.from(lineStr.substring(previous, currentPos));
                        featureList.add(feature);
                        previous = currentPos + 1;
                    }
                    loc.features = featureList.toArray(new FPFeature[]{});
                    locList.add(loc);
                }else { // 楼层信息
                    fpMap.put(Integer.valueOf(lineStr), locList.toArray(new FPLocation[]{}));
                    locList.clear();
                }

            }
            LogUtils.d("generate " + fpMap.size() + " floor fingerprint consume time is " + (SystemClock.elapsedRealtime() - begin));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(bufferedReader);
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(is);
        }
        return fpMap;
    }

    public static void saveStore(String output, int floor, FPLocation[] fps) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(output, true);
            for (FPLocation fp : fps) {
                String content = fp.toString();
                writer.write(content + "\n");
            }
            writer.write(floor + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.closeQuietly(writer);
        }
    }

//    public static void initializeStore(int[] minorArray, Map<Integer, Integer> minorMap,
//                                       Map<Integer, List<ValueInfo>> storeMap) {
//        for(int i = 0;i < minorArray.length;i++) {
//            minorMap.put(minorArray[i], i);
//            storeMap.put(minorArray[i], new ArrayList<ValueInfo>());
//        }
//
//    }

//    public static Map<Integer, List<ValueInfo>> fillStore(String file, int[] minorArray,
//                                                          Map<Integer, List<ValueInfo>> store) {
//        InputStream is = null;
//        InputStreamReader reader = null;
//        BufferedReader bufferedReader = null;
//
//        try {
//            // 获取合法的uuid
//            is = new FileInputStream(file);
//            reader = new InputStreamReader(is);
//            bufferedReader = new BufferedReader(reader);
//            String lineStr;
//            byte value;
//            long begin = SystemClock.elapsedRealtime();
//            // debug
//            int lineCount = 0;
//            // temp
//            List<ValueInfo> temp = new ArrayList<>();
//            while ((lineStr = bufferedReader.readLine()) != null) {
//                int previous = 0;
//                int currentPos;
//                int currentColulmn = 0;
//                while((currentPos = lineStr.indexOf('#', previous)) > -1) {
//                    value = Float.valueOf(lineStr.substring(previous, currentPos)).byteValue();
//                    if(value > -98.0) { // 有效的rssi
//                        List<ValueInfo> valueList = store.get(minorArray[currentColulmn]);
//                        ValueInfo info = new ValueInfo(null, value);
//                        valueList.add(info);
//
//                        temp.add(info);
//                    }
//                    currentColulmn ++;
//                    previous = currentPos + 1;
//                }
//                // fixme 优化位置信息放在行首
//                String locValue = lineStr.substring(previous, lineStr.length());
//                String[] splits = locValue.split("_");
//                LocateInfo lInfo = new LocateInfo();
//                lInfo.x = Float.valueOf(splits[0]);
//                lInfo.y = Float.valueOf(splits[1]);
//                lInfo.floor = Float.valueOf(splits[3]).intValue();
//
//                for(ValueInfo info : temp) {
//                    info.loc = lInfo;
//                }
//                temp.clear();
//
//                LogUtils.i((++lineCount) + " fingerprints was done, location(" + lInfo.x + "," + lInfo.y + "," + lInfo.floor + ")");
//
//            }
////            int memorySize = 0;
////            for(int i = 0;i < minorArray.length;i++) {
////                List<ValueInfo> list = store.get(minorArray[i]);
////                if(list.size() > 0) {
////                    LogUtils.e(minorArray[i] + "(" + i + ")" + "'s finger count ---->" + list.size());
////                }
////                memorySize += 4 + list.size() * 24;
////            }
////            LogUtils.e("memory is " + memorySize);
//            LogUtils.d("consume time is " + (SystemClock.elapsedRealtime() - begin));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.closeQuietly(bufferedReader);
//            IOUtils.closeQuietly(reader);
//            IOUtils.closeQuietly(is);
//        }
//        return store;
//    }
}
