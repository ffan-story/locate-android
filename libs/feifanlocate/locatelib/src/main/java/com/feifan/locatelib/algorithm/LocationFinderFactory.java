package com.feifan.locatelib.algorithm;

import com.feifan.indoorlocation.IIndoorLocator;
import com.feifan.locatelib.algorithm.finder.FullMultiFinder;
import com.feifan.locatelib.algorithm.finder.FullSingleFinder;

/**
 * Created by xuchunlei on 2016/12/1.
 */

public class LocationFinderFactory {
    private static final LocationFinderFactory INSTANCE = new LocationFinderFactory();

    /**
     * 全量匹配计算出单一结果
     * <p>
     *     欧拉距离最小者作为唯一结果
     * </p>
     */
    public static final int FINDER_FULL_MATCH_SINGLE_RESULT = 1;
    /**
     * 全量匹配计算出多个结果
     * <p>
     *     对最小欧拉距离k倍以内的点进行加权平均后，得出结果.当前k = 1.3
     * </p>
     */
    public static final int FINDER_FULL_MATCH_MULTI_RESULT = 2;

    private LocationFinderFactory() {

    }

    public static LocationFinderFactory getInstance() {
        return INSTANCE;
    }

    public ILocationFinder getFinder(int finder) {
        switch (finder) {
            case FINDER_FULL_MATCH_SINGLE_RESULT:
                return FullSingleFinder.getInstance();
            case FINDER_FULL_MATCH_MULTI_RESULT:
                return FullMultiFinder.getInstance();
            default:
                throw new IllegalArgumentException("finder " + finder + " not supported");
        }
    }
}
