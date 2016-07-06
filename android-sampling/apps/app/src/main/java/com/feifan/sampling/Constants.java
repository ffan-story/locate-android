package com.feifan.sampling;

import android.os.Environment;

/**
 * 常量
 * Created by xuchunlei on 16/4/20.
 */
public class Constants {

    private Constants(){

    }

    /**
     * When is offline state ,the remote id is -1
     */
    public static final int DEFAULT_REMOTE_ID = -1;

    public static final String DEFAULT_SCAN_SAMPLES = "10";
    /**
     * 调试时使用的日志TAG
     */
    public static final String DEBUG_TAG = "Sampling";

    /**
     * 数据格式布局-ibeacon
     */
    public static final String LAYOUT_IBEACON = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";

    /** 无效的整型值 */
    public static final int NO_INTEGER = -1;

    /**
     * 保存样本数据的文件路径
     */
    public static final String FILE_PATH_EXPORT_SAMPLE_DATA = Environment.getExternalStorageDirectory().getAbsolutePath();

    /**
     * 参数名－采集点ID
     */
    public static final String EXTRA_KEY_SPOT_ID = "spot_id";

    /**
     * 参数名－采集点ID
     */
    public static final String EXTRA_KEY_SPOT_X = "spot_x";

    /**
     * 参数名－采集点ID
     */
    public static final String EXTRA_KEY_SPOT_Y = "spot_y";
    /**
     * 参数名－采集点名称
     */
    public static final String EXTRA_KEY_SPOT_NAME = "spot_name";
    /**
     * 参数名－采集点方向
     */
    public static final String EXTRA_KEY_SPOT_DIRECTION = "spot_direction";

    /**
     * Loader常量
     */
    public static class LOADER {
        private LOADER(){

        }

        /**
         * Loader标识－采集点
         */
        public final static int LOADER_ID_SPOT = 0;

        /**
         * Loader标识－样本
         */
        public static final int LOADER_ID_SAMPLE = 1;

        /**
         * Loader标识－样本详情
         */
        public static final int LOADER_ID_SAMPLE_DETAIL = 2;

        /**
         * Loader标识－定位区域
         */
        public static final int LOADER_ID_ZONE = 3;

        /**
         * Loader标识－Beacon的UUID
         */
        public static final int LOADER_ID_BEACON_UUID = 4;

    }

    /**
     * 参数名称常量
     */
    public static class EXTRA {

        /** 参数名－ID */
        public final static String KEY_ID = "id";

        /** 参数名－名称 */
        public final static String KEY_NAME = "name";
    }

    public static class SHAREPREFERENCE{
        public final static String LOG_FILE_PATH = "log_file_path";
        public final static String DEFAULT_LOG_FILE_PATH = "indoor";
        public final static String RECYCLE_TIME_INTERVAL = "recycle_time_interval";
        public final static String SCAN_MAX_COUNT = "prefs_general_sample_max_count";
        public final static int DEFAULT_SCAN_TIME = 1000;
        public final static int DEFAULT_SCAN_NUM = 100;
    }
}
