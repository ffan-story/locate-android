package com.feifan.locatelib.algorithm.inspector;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locatelib.algorithm.ILocationInspector;
import com.feifan.locatelib.cache.FingerprintStore.FPLocation;

/**
 * Created by xuchunlei on 2016/12/1.
 */

public class DefaultInspector implements ILocationInspector{

    private float measureValue = 3f;
    private float x;
    private float y;

    @Override
    public FPLocation inspect(FPLocation loc, double minR) {
        if(loc == null) {
            return null;
        }
        double delta = 0;
        if(x == 0 && y == 0) {
            LogUtils.d("init inspector with x,y=" + loc.x + "," + loc.y);
        }else {
            float dx = loc.x - x;
            float dy = loc.y - y;
            delta = Math.sqrt(dx * dx + dy * dy);
        }

        x = loc.x;
        y = loc.y;

        return delta > measureValue ? null : loc;
    }

    @Override
    public void updateThreshold(double[] values) {
        // todo none
    }

    public void setMeasureValue(float value) {
        measureValue = value;
    }

}
