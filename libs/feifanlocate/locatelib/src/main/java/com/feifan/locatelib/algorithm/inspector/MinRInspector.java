package com.feifan.locatelib.algorithm.inspector;

import android.os.Environment;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.debuglib.window.DebugWindow;
import com.feifan.locatelib.BuildConfig;
import com.feifan.locatelib.algorithm.ILocationInspector;
import com.feifan.locatelib.cache.FingerprintStore.FPLocation;
import com.feifan.locatelib.utils.DebugUtils;
import com.feifan.sensorlib.processor.Exporter;

/**
 * 最小结果半径检查器
 *
 * Created by xuchunlei on 2016/12/20.
 */

public class MinRInspector implements ILocationInspector {

    // 结果半径
    private double threshold = 45;
    private double average = 45;
    private double factor = 0.1; // 缩放因子

    private double max_threshold = 65; // 最大阈值
    private double max_factor = 3;
    private double min_threshold = 55; // 最小阈值

    private double weightA = 0.6;
    private double weightM = 0.4;

    // FIXME: remove me later
    private Exporter mExporter = new Exporter();
    {
        mExporter.open(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + "minR" + ".csv");
    }

    @Override
    public FPLocation inspect(FPLocation loc, double minR) {
        FPLocation result = null;
//        DebugWindow.get().logI("delta=" + (minR-threshold) + "minR=" + minR + ",threshold=" + threshold);
        if(minR <= threshold) {
            result = loc;
        }

        doThreshold(minR);
        // FIXME: 2016/12/26 remove me later
//        mExporter.writeLine(minR + "," + threshold + "," + (minR - threshold) + "," + result);

//        average = (average + minR) / 2;

//        DebugWindow.get().log(threshold + "," + max_threshold);
//        LogUtils.e("minR=" + minR + ",阈值=" + threshold + ",delta=" + (minR - threshold));

        return result;
    }

    @Override
    public void updateThreshold(double[] value) {
//        max_threshold = (max_threshold + value * max_factor) / 2;
    }

    // 动态更新阈值
    private void doThreshold(double minR) {
//        if(BuildConfig.DEBUG) {
//            weightA = DebugUtils.Algorithm.MINR_WEIGHT_HISTORY;
//            weightM = DebugUtils.Algorithm.MINR_WEIGHT_CURRENT;
//            min_threshold = DebugUtils.Algorithm.MINR_THRESHOLD_MIN;
//            max_threshold = DebugUtils.Algorithm.MINR_THRESHOLD_MAX;
//            factor = DebugUtils.Algorithm.MINR_SCALE_FACTOR;
//            DebugWindow.get().logI("min->" + min_threshold +
//                                   ",max->" + max_threshold +
//                                   ",weightA->" + weightA +
//                                   ",weightM->" + weightM +
//                                   ",factor->" + factor);
//        }

        average = average * weightA + minR * weightM;
        threshold = average * (1 + factor);
        if(threshold < min_threshold ) {
            threshold = min_threshold;
        }
        if(threshold >= max_threshold) {
            threshold = max_threshold;
        }
    }

}
