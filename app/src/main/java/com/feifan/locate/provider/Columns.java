package com.feifan.locate.provider;

/**
 * Created by xuchunlei on 16/10/14.
 */

public final class Columns {

    /**
     * 点列定义
     */
    public interface SpotColumns {
        /**
         * 字段名－x轴坐标
         *  TYPE:FLOAT
         */
        String X = "x";
        /**
         * 字段名－y轴坐标
         * TYPE:FLOAT
         */
        String Y = "y";
    }

    /**
     * 样本列定义
     */
    public interface SampleColumns {
        /**
         * 字段名－样本名称
         *  TYPE:TEXT
         */
        String _NAME = "name";
        /**
         * 字段名－样本总数
         *  TYPE:INTEGER
         */
        String _TOTAL ="total";
        /**
         * 字段名－采集状态
         *  TYPE:INTEGER
         *  VALUE:
         *  {@link SampleColumns#STATUS_NONE},
         *  {@link SampleColumns#STATUS_READY},
         *  {@link SampleColumns#STATUS_RUNNING},
         *  {@link SampleColumns#STATUS_FINISH}
         */
        String _STATUS = "status";
        /**
         * 字段名－采集进度
         *  TYPE:TEXT
         */
        String _PROGRESS = "progress";

        int STATUS_NONE = 0;
        int STATUS_READY = STATUS_NONE + 1;
        int STATUS_RUNNING = STATUS_READY + 1;
        int STATUS_FINISH = STATUS_RUNNING + 1;
    }
}
