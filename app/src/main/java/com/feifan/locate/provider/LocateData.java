package com.feifan.locate.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.feifan.baselib.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
        /**
         * 字段名－平面图文件名
         */
        public static final String PLAN = "plan";
        /**
         * 字段名-比例尺
         */
        public static final String SCALE = "scale";
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
         * 字段名－移动
         * TYPE:BOOLEAN
         */
        public static final String MOVABLE = "movable";

        /**
         * 字段名－定位区域
         * TYPE:INTEGER
         */
        public static final String ZONE = "zone";

        // 更新
        private static final Map<String, Object> PARAMS = new HashMap<>();

        /**
         * 添加采集点
         * @param context
         */
        public static int add(Context context, float x, float y, int zone) {
            final ContentResolver resolver = context.getContentResolver();
            final int COLUMN_COUNT = 3;
            ContentValues values = new ContentValues(COLUMN_COUNT);
            values.put(X, String.valueOf(x));
            values.put(Y, String.valueOf(y));
            values.put(MOVABLE, true);
            values.put(ZONE, zone);
            Uri result = resolver.insert(CONTENT_URI, values);
            LogUtils.i("workspot:add a new spot(" + x + "," + y + ") at " + zone + " with " + result);
            return Integer.valueOf(result.getLastPathSegment());
        }

        /**
         * 更新采集点
         * @param context
         * @param x
         * @param y
         * @param spotId
         */
        public static void update(Context context, float x, float y, int spotId) {
            final ContentResolver resolver = context.getContentResolver();
            final int COLUMN_COUNT = 2;
            ContentValues values = new ContentValues(COLUMN_COUNT);
            values.put(X, String.valueOf(x));
            values.put(Y, String.valueOf(y));
            int ret = resolver.update(CONTENT_URI, values, _ID + "=?", new String[]{ String.valueOf(spotId) });
            LogUtils.i("workspot:update " + ret + " work spot");
        }

        /**
         * 更新样本点
         * <p>
         *     状态
         * </p>
         * @param context
         * @param movable
         * @param id
         */
        public static void update(Context context, boolean movable, int id) {
            PARAMS.clear();
            PARAMS.put(MOVABLE, movable ? 1 : 0);
            updateData(CONTENT_URI, context, PARAMS, id);
        }

        /**
         * 删除采集点
         * @param context
         * @param id 点索引
         */
        public static boolean remove(Context context, int id) {
            final ContentResolver resolver = context.getContentResolver();
//            int count = resolver.delete(CONTENT_URI, _ID + "=?", new String[]{ String.valueOf(id) });
            int count = resolver.delete(Uri.withAppendedPath(CONTENT_URI, String.valueOf(id)), null, null);
            LogUtils.i("workspot:delete " + count + " spot{ id=" + id + " }");
            return count == 1;
        }

//        /**
//         * 查找采集点
//         * @param context
//         * @param x
//         * @param y
//         * @param zone
//         * @return
//         */
//        public static Cursor find(Context context, float x, float y, int zone) {
//            LogUtils.i("workspot:find spot(" + x + "," + y + ") at zone " + zone);
//            final ContentResolver resolver = context.getContentResolver();
//            return resolver.query(CONTENT_URI, null, X + "=? and " + Y + "=? and " + ZONE + "=?",
//                    new String[]{ String.valueOf(x), String.valueOf(y), String.valueOf(zone) }, null);
//        }

        public static Cursor findMovableSpot(Context context, int zoneId) {
            Map<String, Object> parmas = new HashMap<>();
            parmas.put(MOVABLE, 1);
            parmas.put(ZONE, zoneId);
            return find(CONTENT_URI, context, parmas);
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
         * TYPE:INTEGER
         */
        public static final String COUNT = "count";
        /**
         * 字段名－需要采集样本的总数
         * TYPE:INTEGER
         */
        public static final String TOTAL = "total";
        /**
         * 字段名-采样次数
         * TYPE:INTEGER
         */
        public static final String TIMES = "times";
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

        /** 状态 */
        public static final int STATUS_NONE = 0;
        public static final int STATUS_READY = STATUS_NONE + 1;
        public static final int STATUS_RUNNING = STATUS_READY + 1;
        public static final int STATUS_FINISH = STATUS_RUNNING + 1;

        // 更新
        private static final Map<String, Object> PARAMS = new HashMap<>();

        /**
         * 添加样本点
         * @param context
         */
        public static int add(Context context, float x, float y, float d, int wspot) {
            final ContentResolver resolver = context.getContentResolver();

            final int COLUMN_COUNT = 6;
            ContentValues values = new ContentValues(COLUMN_COUNT);
            values.put(X, x);
            values.put(Y, y);
            values.put(D, d);
            values.put(COUNT, 0);
            values.put(TIMES, 0);
            values.put(STATUS, 1);
            values.put(WORKSPOT, wspot);
            Uri result = resolver.insert(CONTENT_URI, values);
            LogUtils.d("samplespot:insert a samplespot at " + result);
            return Integer.valueOf(result.getLastPathSegment());
        }

        /**
         * 更新样本点
         * <p>
         *     数量、扫描次数和状态
         * </p>
         * @param context
         */
        public static void updateScan(Context context, int count, int times, int id, int status) {
            if(context == null) {
                return;
            }
            PARAMS.clear();
            PARAMS.put(TIMES, times);
            PARAMS.put(COUNT, count);
            PARAMS.put(STATUS, status);
            updateData(CONTENT_URI, context, PARAMS, id);
        }

        /**
         * 更新配置
         * @param context
         * @param total
         * @param id
         */
        public static void updateConfig(Context context, int total, int id) {
            PARAMS.clear();
            PARAMS.put(TOTAL, total);
            updateData(CONTENT_URI, context, PARAMS, id);
        }

        /**
         * 更新样本点
         * <p>
         *     方向
         * </p>
         * @param context
         * @param direction
         * @param id
         */
        public static void update(Context context, float direction, int id) {
            PARAMS.clear();
            PARAMS.put(D, direction);
            updateData(CONTENT_URI, context, PARAMS, id);
        }

        public static Cursor findByStatus(Context context, int status, int workSpotId) {
            PARAMS.clear();
            PARAMS.put(STATUS, status);
            PARAMS.put(WORKSPOT, workSpotId);
            return find(CONTENT_URI, context, PARAMS);
        }
    }

    private static ContentValues createValues(Map<String, Object> params) {
        final Set<Entry<String, Object>> entrySet = params.entrySet();
        final int COLUMN_COUNT = 5;

        ContentValues values = new ContentValues(COLUMN_COUNT);
        for(Entry<String, Object> entry : entrySet) {
            Object value = entry.getValue();
            if(value instanceof Integer) {
                values.put(entry.getKey(), (Integer)value);
            }else if(value instanceof Float) {
                values.put(entry.getKey(), (Float)value);
            }else if(value instanceof String) {
                values.put(entry.getKey(), value.toString());
            }else {
                throw new IllegalArgumentException(value.getClass().getName() + " not supported");
            }
        }
        return  values;
    }

    /**
     * 查找
     * @param uri
     * @param context
     * @param params
     * @return
     */
    private static Cursor find(Uri uri, Context context, Map<String, Object> params) {
        LogUtils.d("find from " + uri.toString() + " with " + params.toString());
        final ContentResolver resolver = context.getContentResolver();
        Set<Entry<String, Object>> entrySet = params.entrySet();
        String selection = "";
        List<String> selectionArgs = new ArrayList<>();
        for(Entry<String, Object> entry : entrySet) {
            selection += entry.getKey() + "=? and ";
            selectionArgs.add(entry.getValue().toString());
        }
        selection = selection.substring(0, selection.length() - 5);

        return resolver.query(uri, null, selection, selectionArgs.toArray(new String[0]), null);
    }

    private static void updateData(Uri uri, Context context, Map<String, Object> params, int id) {
        final ContentResolver resolver = context.getContentResolver();
        ContentValues values = createValues(params);
        int ret = resolver.update(uri, values, BaseColumns._ID + "=?", new String[]{ String.valueOf(id) });
        LogUtils.d("update " + ret + " item at " + uri.toString() + " with " + params);
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
