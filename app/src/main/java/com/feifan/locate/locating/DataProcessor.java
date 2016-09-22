package com.feifan.locate.locating;

import com.feifan.scanlib.beacon.SampleBeacon;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 实时数据处理器
 * <p>
 *     目前支持：
 *     （1）beacon
 * </p>
 * Created by xuchunlei on 16/9/19.
 */
public class DataProcessor {

    private DataProcessor() {

    }

    public synchronized static Map<String, Float> processBeaconData(Collection<SampleBeacon> data) {
        Map<String, Float> result = new HashMap<>();
        result.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_0.0_61249.0", -69.95f);
        result.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_0.0_61246.0", -57.85f);
        result.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_0.0_61270.0", -73.35f);
        result.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_21112.0_64497.0", -78.85f);
        result.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_0.0_61242.0", -55.9f);
        result.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_0.0_61288.0", -75.85f);
        result.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_0.0_61243.0", -67.65f);
        result.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_0.0_61251.0", -70.15f);
        result.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_0.0_61250.0", -72.3f);
        result.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_0.0_61320.0", -84.75f);

        return result;
    }
}
