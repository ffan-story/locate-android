package com.feifan.locatelib.inertial;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locatelib.cache.FingerprintStore.FPLocation;

/**
 * 动作检测器
 * Created by xuchunlei on 2016/12/14.
 */

public class Predictor {

    // pedo:为减少变量创建次数，均使用成员变量
    private static final float MIN_PEAK_THRESHOLD = 11f;       // 波峰最小阈值(经验值)
    private static final float MAX_PEAK_THRESHOLD = 19.6f;     // 波峰最大阈值(经验值)
    private static final long MIN_STEP_TIME_SPAN = 200;        // 单步最小时间间隔(经验值)
    private static final long MAX_STEP_TIME_SPAN = 2000;       // 单步最大时间间隔(经验值)
    private static final float MIN_AMPLITUDE = 1.7f;           // 单步最小振幅（经验值）
    private static final int AMPLITUDE_WINDOW_SIZE = 5;        // 振幅阈值滑动窗口尺寸
    private static final float STEP_LENGTH = 0.6f;             // 单步步长

    private float meanValue; // 到原点的几何距离

    // 步行检测因素-波峰变化
    private float lastValue = 0; // 上一次传感器数值
    private boolean lastUp = false; // 上一次计算区间是否是增函数区间
    private boolean isUp = false;   // 计算区间是否是增函数区间
    private boolean stepFlag = false; // 步行标记

    // 步行检测因素-时间
    private long lastStamp = 0;     // 上一次检测到波峰的时间戳
    private long nowStamp = 0;      // 本次检测到波峰的时间戳
    private long span = 0;          // 波峰时间间隔

    // 步行检测因素-振幅:受行进速度影响，阈值取最近4次平均值计算
    private float crestValue;       // 波峰值
    private float troughValue;      // 波谷值
    private float amplitude;        // 振幅
    private float amplitude_threshold = 2.0f; // 振幅阈值(初始值为经验值)
    private float[] amplitudeArray = new float[]{
            MIN_AMPLITUDE,
            MIN_AMPLITUDE,
            MIN_AMPLITUDE,
            MIN_AMPLITUDE,
            MIN_AMPLITUDE
    }; // 阈值计算窗口
    private float meanAmplitude;

    // 预测行进距离
    private int step;
    private volatile float dx;
    private volatile float dy;

    private FPLocation mLocation = new FPLocation();

    // temp
    private double rotation =  Math.PI / 2;
//    private double rotation =  0;

    // test
//    private Exporter mExporter = new Exporter();
//    public Predictor() {
//        mExporter.open(Environment.getExternalStorageDirectory().getAbsolutePath() + "/position.csv");
//    }

    /**
     * 计算位置
     * @param x
     * @param y
     * @param z
     */
    public void computeStep(float x, float y, float z, float azimuth) {
        // 原点的几何距离进行计算，减少误差
        meanValue = (float) Math.sqrt(Math.pow(x, 2)
                + Math.pow(y, 2) + Math.pow(z, 2));
        if(detectStep(meanValue)) {
            dx += STEP_LENGTH * Math.sin(azimuth - rotation);
            dy += STEP_LENGTH * Math.cos(azimuth - rotation);
        }
//        DebugWindow.get().log("dx,dy=" + dx + "," + dy);
//        mExporter.writeLine(dx + "," + dy + ",azimuth=" + azimuth);
    }

    public FPLocation updatePredictedLocation() {
        if(mLocation != null) {
            mLocation.x += dx;
            mLocation.y += dy;
            dx = 0;
            dy = 0;
        }

        return mLocation;
    }

    /**
     * 设置参考位置
     * @param location
     */
    public void setReference(FPLocation location) {
//        mLocation = location;
        mLocation.x = location.x;
        mLocation.y = location.y;
        mLocation.floor = location.floor;
        step = 0;
        dx = 0;
        dy = 0;
        LogUtils.i("update a valid location:" + location.x + "," + location.y + "," + location.floor);
    }

    public boolean isInited() {
        return mLocation.floor !=0;
    }

    private boolean detectStep(float value) {
        stepFlag = false;
        if(lastValue != 0) { // 计算开始
            if(detectPeak(value, lastValue)) { // 波峰
                lastStamp = nowStamp;
                span = System.currentTimeMillis() - lastStamp;
                amplitude = crestValue - troughValue;

                if(span >= MIN_STEP_TIME_SPAN
                        && span <= MAX_STEP_TIME_SPAN) { // 满足时间阈值
                    if(amplitude >= amplitude_threshold) { // 满足振幅阈值
                        step++;
                        stepFlag = true;
                    }

                    // 更新波峰周期开始时间
                    nowStamp = span + lastStamp;
                }
                // 排除抖动，更新振幅阈值和波峰周期开始时间
                if(span >= MIN_STEP_TIME_SPAN && amplitude >= MIN_AMPLITUDE) {
                    nowStamp = span + lastStamp;
                    amplitude_threshold = updateAmplitude(amplitude);
                }

//                showLog("detect a new peak");
            }
        }
        lastValue = value;
        return stepFlag;
    }

    // 检测波峰，波峰是计算步数的必要条件
    private boolean detectPeak(float newValue, float lastValue) {
        lastUp = isUp;
        if(newValue >= lastValue) { // 增函数区间
            isUp = true;
        } else { // 减函数区间
            isUp = false;
        }

        if(!isUp && lastUp) { // 检测到可疑波峰
            if(lastValue >= MIN_PEAK_THRESHOLD && lastValue < MAX_PEAK_THRESHOLD) { // 峰值在阈值范围以内
                crestValue = lastValue; // 记录波峰值
                return true;
            }
        }else if(isUp && !lastUp) { // 检测到波谷
            troughValue = lastValue; // 记录波谷值
            return false;
        }
        return false;
    }

    private float updateAmplitude(float value) {
        amplitude_threshold = meanValue(amplitudeArray);
        for(int i = 1;i < AMPLITUDE_WINDOW_SIZE;i++) {
            amplitudeArray[i - 1] = amplitudeArray[i];
        }
        amplitudeArray[AMPLITUDE_WINDOW_SIZE - 1] = value;
        return amplitude_threshold;
    }

    // 考虑到行进速度非匀速，使用最近若干次步幅均值计算阈值，计算中涉及数值均为经验值
    private float meanValue(float value[]) {
        meanAmplitude = 0;
        for (int i = 0; i < AMPLITUDE_WINDOW_SIZE; i++) {
            meanAmplitude += value[i];
        }
        meanAmplitude = meanAmplitude / AMPLITUDE_WINDOW_SIZE;
        if (meanAmplitude >= 8) {
            meanAmplitude = (float) 4.3;
        } else if (meanAmplitude >= 7 && meanAmplitude < 8) {
            meanAmplitude = (float) 3.3;
        } else if (meanAmplitude >= 4 && meanAmplitude < 7) {
            meanAmplitude = (float) 2.3;
        } else if (meanAmplitude >= 3 && meanAmplitude < 4) {
            meanAmplitude = (float) 2.0;
        } else {
            meanAmplitude = (float) 1.7;
        }
        return meanAmplitude;
    }

}
