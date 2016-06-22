package com.feifan.sampling.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Sample操作类
 *
 * Created by xuchunlei on 16/4/21.
 */
public class SampleData {

    /**
     * SampleProvider使用的权限字符串
     */
    public static final String AUTHORITY = "com.feifan.sampling.sample";

    /**
     * 定位区域定义
     */
    public static class Zone implements BaseColumns {

        /** 访问Zone表的URL */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/zone");

        /** {@link com.feifan.sampling.provider.SampleData.Zone#CONTENT_URI}的MIME类型 */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.feifan.zone";

        /** {@link com.feifan.sampling.provider.SampleData.Zone#CONTENT_URI}子项的MIME类型 */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.feifan.zone";

        /**
         * 字段名－名称
         *  TYPE:TEXT
         */
        public static final String NAME = "name";
        /**
         * 服务器端返回的id
         */
        public static final String REMOTE_ID = "remote_id";

    }


    /**
     * 采集点定义
     */
    public static class Spot implements BaseColumns {

        /** 访问Spot表的URL */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/spot");

        /** {@link com.feifan.sampling.provider.SampleData.Spot#CONTENT_URI}的MIME类型 */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.feifan.spot";

        /** {@link com.feifan.sampling.provider.SampleData.Spot#CONTENT_URI}子项的MIME类型 */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.feifan.spot";

        /**
         * 字段名－x轴坐标
         *  TYPE:FLOAT
         */
        public static final String X = "x";
        /**
         * 字段名－y轴坐标
         * TYPE:FLOAT
         */
        public static final String Y = "y";
        /**
         * 字段名－方向（角度）
         * TYPE:FLOAT
         */
        public static final String D = "D";

        public static final String NAME = "name";

        /**
         * 字段名－定位区域
         * TYPE:INTEGER
         */
        public static final String ZONE = "zone";

        /**
         * 服务器端返回的id
         */
        public static final String REMOTE_ID = "remote_id";
    }

    /**
     * 样本定义
     */
    public static class Sample implements BaseColumns {

        /** 访问Sample表的URL */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/sample");

        /** {@link com.feifan.sampling.provider.SampleData.Sample#CONTENT_URI}的MIMIE类型 */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.feifan.sample";

        /** {@link com.feifan.sampling.provider.SampleData.Sample#CONTENT_URI}子项的MIMIE类型 */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.feifan.sample";

        /** 名称 */
        public static final String NAME = "name";

        /** 字段名－采样时间
         * decimal
         */
        public static final String TIME = "time";

        /** 采集点 */
        public static final String SPOT = "spot";

    }

    /**
     * com.my.com.my.com.mm.beacon-uuid数据定义
     */
    public static class BeaconUUID implements BaseColumns {

        /** 访问Samples表的URL */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/beacon_uuid");

        /** {@link com.feifan.sampling.provider.SampleData.BeaconUUID#CONTENT_URI}的MIMIE类型 */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.feifan.beacon_uuid";

        /** {@link com.feifan.sampling.provider.SampleData.BeaconUUID#CONTENT_URI}子项的MIMIE类型 */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.feifan.beacon_uuid";

        /** 字段名－uuid
         *  TYPE:TEXT
         */
        public static final String UUID = "uuid";

    }

    /**
     * beacon数据定义
     */
    public static class BeaconDetail implements BaseColumns {
        /** 访问BeaconDetail表的URL */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/beacon");

        /** 使用Sample参数访问BeaconDetail表的URL */
        public static final Uri CONTENT_URI_SAMPLE = Uri.parse("content://" + AUTHORITY + "/beacon/sample");

        /** {@link com.feifan.sampling.provider.SampleData.BeaconDetail#CONTENT_URI}的MIMIE类型 */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.feifan.beacon";

        /** {@link com.feifan.sampling.provider.SampleData.BeaconDetail#CONTENT_URI}子项的MIMIE类型 */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.feifan.beacon";

        /** 字段名－uuid
         *  TYPE:TEXT
         */
        public static final String UUID = "uuid";

        /** 字段名－major
         *  TYPE:INTEGER
         */
        public static final String MAJOR = "major";

        /** 字段名－minor
         *  TYPE:INTEGER
         */
        public static final String MINOR = "minor";

        /** 字段名－rssi
         *  TYPE:INTEGER
         */
        public static final String RSSI = "rssi";

        /** 字段名－mac
         *  TYPE:INTEGER
         */
        public static final String MAC = "mac";

        /** 字段名－time
         *  TYPE:TEXT
         */
        public static final String TIME = "time";

        /**
         * 字段名－精确度（距离）
         * TYPE:DECIMAL
         */
        public static final String ACCURACY = "accuracy";

        /**
         * 字段名－方向
         * TYPE:DECIMAL
         */
        public static final String DIRECTION = "direction";

        /** 字段名－样本（外键）
         *  TYPE:INTEGER
         *  FOREIGN KEY:Sample(_id)
         */
        public static final String SAMPLE = "sample";

        public static final String REMOTE_ID = "remoteid";
    }

    /**
     * 样本详情数据定义
     */
    public static class SampleDetail {

        /** 访问BeaconDetail表的URL */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/sample_detail");

        /** {@link com.feifan.sampling.provider.SampleData.SampleDetail#CONTENT_URI}的MIMIE类型 */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.feifan.sample_detail";

        /** {@link com.feifan.sampling.provider.SampleData.SampleDetail#CONTENT_URI}子项的MIMIE类型 */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.feifan.sample_detail";
    }
}
