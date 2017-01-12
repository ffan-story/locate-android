package com.feifan.locatelib.utils;

/**
 * Created by xuchunlei on 2017/1/3.
 */

public class DebugUtils {

    private DebugUtils() {

    }

    public static class Algorithm {

        /**
         * 最小半径合法值的最大阈值
         */
        public static double MINR_THRESHOLD_MAX = 75;

        /**
         * 最小半径合法值的最小阈值
         */
        public static double MINR_THRESHOLD_MIN = 45;

        /**
         * 最小半径历史数据权重
         */
        public static double MINR_WEIGHT_HISTORY = 0.6;

        /**
         * 最小半径当前数据权重
         */
        public static double MINR_WEIGHT_CURRENT = 1 - MINR_WEIGHT_HISTORY;

        /**
         * 计算实际阈值时使用的缩放因子
         * 越大表示越倾向于使用指纹定位，否则越倾向于使用PDR定位
         */
        public static double MINR_SCALE_FACTOR = 0.1;

        private Algorithm() {

        }
    }
}
