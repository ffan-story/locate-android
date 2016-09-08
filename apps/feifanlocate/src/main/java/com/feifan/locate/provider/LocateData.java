package com.feifan.locate.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.feifan.baselib.utils.LogUtils;

/**
 * Sample操作类
 *
 * Created by xuchunlei on 16/4/21.
 */
public class LocateData {

    /**
     * SampleProvider使用的权限字符串
     */
    public static final String AUTHORITY = "com.feifan.locate";

    /**
     * 定位区域定义
     */
    public static class Zone implements BaseColumns {

        /** 访问Zone表的URL */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/zone");

        /** {@link Zone#CONTENT_URI}的MIME类型 */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.feifan.zone";

        /** {@link Zone#CONTENT_URI}子项的MIME类型 */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.feifan.zone";

        /**
         * 字段名－名称
         *  TYPE:TEXT
         */
        public static final String NAME = "name";
    }


    /**
     * 采集点定义
     */
    public static class WorkSpot implements BaseColumns {

        /** 访问Spot表的URL */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/workspot");

        /** {@link WorkSpot#CONTENT_URI}的MIME类型 */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.feifan.workspot";

        /** {@link WorkSpot#CONTENT_URI}子项的MIME类型 */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.feifan.workspot";

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
         * 字段名－定位区域
         * TYPE:INTEGER
         */
        public static final String ZONE = "zone";

        /**
         * 添加采集点
         * @param context
         */
        public static void add(Context context, float x, float y, int zone) {
            final ContentResolver resolver = context.getContentResolver();
            final int COLUMN_COUNT = 5;
            ContentValues values = new ContentValues(COLUMN_COUNT);
            values.put(X, String.valueOf(x));
            values.put(Y, String.valueOf(y));
            values.put(ZONE, zone);
            Uri result = resolver.insert(CONTENT_URI, values);
            LogUtils.i("workspot:add a new spot(" + x + "," + y + ") at " + zone + " with " + result);
        }

        /**
         * 删除采集点
         * @param context
         * @param x
         * @param y
         * @param zone
         */
        public static void remove(Context context, float x, float y, int zone) {
            final ContentResolver resolver = context.getContentResolver();
            int count = resolver.delete(CONTENT_URI, X + "=? and " + Y + "=? and " + ZONE + "=?",
                    new String[]{ String.valueOf(x), String.valueOf(y), String.valueOf(zone) });
            LogUtils.i("workspot:delete " + count + " spot(" + x + "," + y + ") at zone " + zone);
        }

        /**
         * 查找采集点
         * @param context
         * @param x
         * @param y
         * @param zone
         * @return
         */
        public static Cursor find(Context context, float x, float y, int zone) {
            LogUtils.i("workspot:find spot(" + x + "," + y + ") at zone " + zone);
            final ContentResolver resolver = context.getContentResolver();
            return resolver.query(CONTENT_URI, null, X + "=? and " + Y + "=? and " + ZONE + "=?",
                    new String[]{ String.valueOf(x), String.valueOf(y), String.valueOf(zone) }, null);
        }

        // temp
        public static Cursor findAll(Context context) {
            final ContentResolver resolver = context.getContentResolver();
            return resolver.query(CONTENT_URI, null, null, null, null);
        }

    }

    /**
     * 样本点定义
     * <pre>
     *     一个采集点可以根据方向不同，拥有多个样本点
     * </pre>
     */
    public static class SampleSpot implements BaseColumns {

        /** 访问Spot表的URL */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/samplespot");

        /** {@link SampleSpot#CONTENT_URI}的MIME类型 */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.feifan.samplespot";

        /** {@link SampleSpot#CONTENT_URI}子项的MIME类型 */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.feifan.samplespot";

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
         * 字段名－方向
         * TYPE:FLOAT
         */
        public static final String D = "d";
        /**
         * 字段名－已经采集样本的数量
         */
        public static final String COUNT = "count";
        /**
         * 字段名－状态
         * TYPE:INTEGER
         * 1-就绪；2-运行；3-暂停；4-完成
         */
        public static final String STATUS = "status";
        /**
         * 字段名－采集点
         * TYPE:INTEGER
         */
        public static final String WORKSPOT = "workspot";

        /**
         * 添加样本点
         * @param context
         */
        public static void add(Context context, float x, float y, float d, int wspot) {
            final ContentResolver resolver = context.getContentResolver();

            final int COLUMN_COUNT = 6;
            ContentValues values = new ContentValues(COLUMN_COUNT);
            values.put(X, x);
            values.put(Y, y);
            values.put(D, d);
            values.put(COUNT, 0);
            values.put(STATUS, 1);
            values.put(WORKSPOT, wspot);
            Uri result = resolver.insert(CONTENT_URI, values);
            LogUtils.d("samplespot:insert a samplespot at " + result);
        }

        /**
         * 更新样本点
         * @param context
         */
        public static void update(Context context) {

        }

    }


//
//    /**
//     * 样本定义
//     */
//    public static class Sample implements BaseColumns {
//
//        /** 访问Sample表的URL */
//        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/sample");
//
//        /** {@link Sample#CONTENT_URI}的MIMIE类型 */
//        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.feifan.sample";
//
//        /** {@link Sample#CONTENT_URI}子项的MIMIE类型 */
//        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.feifan.sample";
//
//        /** 名称 */
//        public static final String NAME = "name";
//
//        /** 字段名－采样时间
//         * decimal
//         */
//        public static final String TIME = "time";
//
//        /** 采集点 */
//        public static final String SPOT = "spot";
//
//    }
//
//    /**
//     * com.my.com.my.com.mm.beacon-uuid数据定义
//     */
//    public static class BeaconUUID implements BaseColumns {
//
//        /** 访问Samples表的URL */
//        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/beacon_uuid");
//
//        /** {@link BeaconUUID#CONTENT_URI}的MIMIE类型 */
//        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.feifan.beacon_uuid";
//
//        /** {@link BeaconUUID#CONTENT_URI}子项的MIMIE类型 */
//        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.feifan.beacon_uuid";
//
//        /** 字段名－uuid
//         *  TYPE:TEXT
//         */
//        public static final String UUID = "uuid";
//
//    }
//
//    /**
//     * beacon数据定义
//     */
//    public static class BeaconDetail implements BaseColumns {
//        /** 访问BeaconDetail表的URL */
//        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/beacon");
//
//        /** 使用Sample参数访问BeaconDetail表的URL */
//        public static final Uri CONTENT_URI_SAMPLE = Uri.parse("content://" + AUTHORITY + "/beacon/sample");
//
//        /** {@link BeaconDetail#CONTENT_URI}的MIMIE类型 */
//        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.feifan.beacon";
//
//        /** {@link BeaconDetail#CONTENT_URI}子项的MIMIE类型 */
//        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.feifan.beacon";
//
//        /** 字段名－uuid
//         *  TYPE:TEXT
//         */
//        public static final String UUID = "uuid";
//
//        /** 字段名－major
//         *  TYPE:INTEGER
//         */
//        public static final String MAJOR = "major";
//
//        /** 字段名－minor
//         *  TYPE:INTEGER
//         */
//        public static final String MINOR = "minor";
//
//        /** 字段名－rssi
//         *  TYPE:INTEGER
//         */
//        public static final String RSSI = "rssi";
//
//        /** 字段名－mac
//         *  TYPE:INTEGER
//         */
//        public static final String MAC = "mac";
//
//        /** 字段名－time
//         *  TYPE:TEXT
//         */
//        public static final String TIME = "time";
//
//        /**
//         * 字段名－精确度（距离）
//         * TYPE:DECIMAL
//         */
//        public static final String ACCURACY = "accuracy";
//
//        /**
//         * 字段名－方向
//         * TYPE:DECIMAL
//         */
//        public static final String DIRECTION = "direction";
//
//        /** 字段名－样本（外键）
//         *  TYPE:INTEGER
//         *  FOREIGN KEY:Sample(_id)
//         */
//        public static final String SAMPLE = "sample";
//
//        public static final String REMOTE_ID = "remoteid";
//    }
//
//    /**
//     * 样本详情数据定义
//     */
//    public static class SampleDetail {
//
//        /** 访问BeaconDetail表的URL */
//        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/sample_detail");
//
//        /** {@link SampleDetail#CONTENT_URI}的MIMIE类型 */
//        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.feifan.sample_detail";
//
//        /** {@link SampleDetail#CONTENT_URI}子项的MIMIE类型 */
//        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.feifan.sample_detail";
//    }
}
