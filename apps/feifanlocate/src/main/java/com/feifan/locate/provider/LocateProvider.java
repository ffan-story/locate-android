package com.feifan.locate.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.provider.LocateData.Zone;
import com.feifan.locate.provider.LocateData.WorkSpot;
import com.feifan.locate.provider.LocateData.SampleSpot;

/**
 * Created by xuchunlei on 16/4/21.
 */
public class LocateProvider extends ContentProvider {

    private static final String AUTHORITY = LocateData.AUTHORITY;

    // 数据库名称
    private static final String DATABASE_NAME = "sample.db";
    // 数据库版本
    private static final int DATABASE_VERSION = 1;

    // 数据库表名－定位区域
    private static final String ZONE_TABLE_NAME = "zone";
    // 数据库表名－采集点
    private static final String WORKSPOT_TABLE_NAME = "workspot";
    // 数据库表名－样本点
    private static final String SAMPLESPOT_TABLE_NAME = "samplespot";
    // 数据库表名－样本
    private static final String SAMPLE_TABLE_NAME = "sample";
    // 数据库表名－BeaconUUID
    private static final String BEACON_UUID_TABLE_NAME = "beacon_uuid";
    // 数据库表名－Beacon
    private static final String BEACON_TABLE_NAME = "beacon";
    // 数据库视图名－SampleDetail
    private static final String SAMPLE_DETAIL_VIEW_NAME = "sample_detail";

    // 数据库实例
    private DatabaseHelper mOpenHelper;

    private static UriMatcher sUriMatcher;
    private static final int ZONE = 1;
    private static final int ZONE_ID = ZONE + 1;
    private static final int WORKSPOT = ZONE_ID + 1;
    private static final int WORKSPOT_ID = WORKSPOT + 1;
    private static final int SAMPLESPOT = WORKSPOT_ID + 1;
    private static final int SAMPLESPOT_ID = SAMPLESPOT + 1;

/*    private static final int SAMPLE = SPOT_ID + 1;
    private static final int SAMPLE_ID = SAMPLE + 1;
    private static final int BEACON_UUID = SAMPLE_ID + 1;
    private static final int BEACON_UUID_ID = BEACON_UUID + 1;
    private static final int BEACON_DETAIL = BEACON_UUID_ID + 1;
    private static final int BEACON_DETAIL_ID = BEACON_DETAIL + 1;
    private static final int BEACON_DETAIL_SAMPLE = BEACON_DETAIL_ID + 1; // 外键－样本
    private static final int SAMPLE_DETAIL = BEACON_DETAIL_SAMPLE + 1;*/
//    private static final int SAMPLE_DETAIL_ID = SAMPLE_DETAIL + 1;

    // 定位区域表列映射集合
    private static HashMap<String, String> sZoneProjectionMap;
    // 采集点表列映射集合
    private static HashMap<String, String> sWorkSpotProjectionMap;
    // 样本点表列映射集合
    private static HashMap<String, String> sSampleSpotProjectionMap;
    // 样本表列映射集合
//    private static HashMap<String, String> sSampleProjectionMap;
    // 样本详情表列映射集合
//    private static LinkedHashMap<String, String> sSampleDetailProjectionMap;
    // Beacon样本详情表列映射集合
//    private static HashMap<String, String> sBeaconDetailProjectionMap;
    // Beacon的uuid表映射集合
//    private static HashMap<String, String> sBeaconUUIDProjectionMap;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "zone", ZONE);
        sUriMatcher.addURI(AUTHORITY, "zone/#", ZONE_ID);
        sUriMatcher.addURI(AUTHORITY, "workspot", WORKSPOT);
        sUriMatcher.addURI(AUTHORITY, "workspot/#", WORKSPOT_ID);
        sUriMatcher.addURI(AUTHORITY, "samplespot", SAMPLESPOT);
        sUriMatcher.addURI(AUTHORITY, "samplespot/#", SAMPLESPOT_ID);
        /*sUriMatcher.addURI(AUTHORITY, "sample", SAMPLE);
        sUriMatcher.addURI(AUTHORITY, "sample/#", SAMPLE_ID);
        sUriMatcher.addURI(AUTHORITY, "beacon_uuid", BEACON_UUID);
        sUriMatcher.addURI(AUTHORITY, "beacon_uuid/#", BEACON_UUID_ID);
        sUriMatcher.addURI(AUTHORITY, "beacon", BEACON_DETAIL);
        sUriMatcher.addURI(AUTHORITY, "beacon/#", BEACON_DETAIL_ID);
        sUriMatcher.addURI(AUTHORITY, "beacon/sample/#", BEACON_DETAIL_SAMPLE);
        sUriMatcher.addURI(AUTHORITY, "sample_detail", SAMPLE_DETAIL);*/

        sZoneProjectionMap = new HashMap<String, String>();
        sZoneProjectionMap.put(Zone._ID, Zone._ID);
        sZoneProjectionMap.put(Zone.NAME, Zone.NAME);
//        sZoneProjectionMap.put(Zone.REMOTE_ID, Zone.REMOTE_ID);

        sWorkSpotProjectionMap = new HashMap<String, String>();
        sWorkSpotProjectionMap.put(WorkSpot._ID, WorkSpot._ID);
        sWorkSpotProjectionMap.put(WorkSpot.X, WorkSpot.X);
        sWorkSpotProjectionMap.put(WorkSpot.Y, WorkSpot.Y);
        sWorkSpotProjectionMap.put(WorkSpot.ZONE, WorkSpot.ZONE);

        sSampleSpotProjectionMap = new HashMap<String, String>();
        sSampleSpotProjectionMap.put(SampleSpot._ID, SampleSpot._ID);
        sSampleSpotProjectionMap.put(SampleSpot.X, SampleSpot.X);
        sSampleSpotProjectionMap.put(SampleSpot.Y, SampleSpot.Y);
        sSampleSpotProjectionMap.put(SampleSpot.D, SampleSpot.D);
        sSampleSpotProjectionMap.put(SampleSpot.STATUS, SampleSpot.STATUS);
        sSampleSpotProjectionMap.put(SampleSpot.COUNT, SampleSpot.COUNT);

        /*sSampleProjectionMap = new HashMap<String, String>();
        sSampleProjectionMap.put(Sample._ID, Sample._ID);
        sSampleProjectionMap.put(Sample.NAME, Sample.NAME);
        sSampleProjectionMap.put(Sample.TIME, Sample.TIME);

        sSampleDetailProjectionMap = new LinkedHashMap<>();
        sSampleDetailProjectionMap.put(BeaconDetail.UUID, BeaconDetail.UUID);
        sSampleDetailProjectionMap.put(BeaconDetail.MAJOR, BeaconDetail.MAJOR);
        sSampleDetailProjectionMap.put(BeaconDetail.MINOR, BeaconDetail.MINOR);
        sSampleDetailProjectionMap.put(BeaconDetail.RSSI, BeaconDetail.RSSI);
        sSampleDetailProjectionMap.put(BeaconDetail.ACCURACY, BeaconDetail.ACCURACY);
        sSampleDetailProjectionMap.put(BeaconDetail.DIRECTION, BeaconDetail.DIRECTION);
        sSampleDetailProjectionMap.put(Sample.TIME, Sample.TIME);
        sSampleDetailProjectionMap.put(Spot.X, Spot.X);
        sSampleDetailProjectionMap.put(Spot.Y, Spot.Y);
        sSampleDetailProjectionMap.put(Spot.D, Spot.D);

        sBeaconDetailProjectionMap = new HashMap<String, String>();
        sBeaconDetailProjectionMap.put(BeaconDetail._ID, BeaconDetail._ID);
        sBeaconDetailProjectionMap.put(BeaconDetail.UUID, BeaconDetail.UUID);
        sBeaconDetailProjectionMap.put(BeaconDetail.MAJOR, BeaconDetail.MAJOR);
        sBeaconDetailProjectionMap.put(BeaconDetail.MINOR, BeaconDetail.MINOR);

        sBeaconUUIDProjectionMap = new HashMap<String, String>();
        sBeaconUUIDProjectionMap.put(BeaconUUID._ID, BeaconUUID._ID);
        sBeaconUUIDProjectionMap.put(BeaconUUID.UUID, BeaconUUID.UUID);*/
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case ZONE:          // 查询定位区域整表
                qb.setTables(ZONE_TABLE_NAME);
                qb.setProjectionMap(sZoneProjectionMap);
                break;
            case WORKSPOT:
                qb.setTables(WORKSPOT_TABLE_NAME);
                qb.setProjectionMap(sWorkSpotProjectionMap);
                break;
            case SAMPLESPOT:
                qb.setTables(SAMPLESPOT_TABLE_NAME);
                qb.setProjectionMap(sSampleSpotProjectionMap);
                break;
            /*case SAMPLE:        // 查询样本整表
                qb.setTables(SAMPLE_TABLE_NAME);
                qb.setProjectionMap(sSampleProjectionMap);
                break;
            case SAMPLE_ID:     // 查询样本单条记录
                qb.setTables(SAMPLE_TABLE_NAME);
                qb.setProjectionMap(sSampleProjectionMap);
                qb.appendWhere(BaseColumns._ID + "=" + uri.getPathSegments().get(1));
                break;
            case BEACON_DETAIL_SAMPLE: // 查询属于样本的beacon详情
                qb.setTables(BEACON_TABLE_NAME);
                qb.setProjectionMap(sBeaconDetailProjectionMap);
                qb.appendWhere(BeaconDetail.SAMPLE + "=" + uri.getLastPathSegment());
                break;
            case SAMPLE_DETAIL: // 查询样本详情
                qb.setTables(SAMPLE_DETAIL_VIEW_NAME);
                qb.setProjectionMap(sSampleDetailProjectionMap);
                break;
            case BEACON_UUID:   // 查询BeaconUUID整表
                qb.setTables(BEACON_UUID_TABLE_NAME);
                qb.setProjectionMap(sBeaconUUIDProjectionMap);
                break;*/
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        // 执行查询
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, null);
        // 设置监听游标的uri，当数据变更时通过该uri可监控到
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case ZONE:
                return Zone.CONTENT_TYPE;
            case ZONE_ID:
                return Zone.CONTENT_ITEM_TYPE;
            case WORKSPOT:
                return WorkSpot.CONTENT_TYPE;
            case WORKSPOT_ID:
                return WorkSpot.CONTENT_ITEM_TYPE;
            case SAMPLESPOT:
                return SampleSpot.CONTENT_TYPE;
            case SAMPLESPOT_ID:
                return SampleSpot.CONTENT_ITEM_TYPE;
            /*case SAMPLE:
                return Sample.CONTENT_TYPE;
            case SAMPLE_ID:
                return Sample.CONTENT_ITEM_TYPE;
            case BEACON_UUID:
                return BeaconUUID.CONTENT_TYPE;
            case BEACON_UUID_ID:
                return BeaconUUID.CONTENT_ITEM_TYPE;
            case BEACON_DETAIL:
                return BeaconDetail.CONTENT_TYPE;
            case BEACON_DETAIL_ID:
            case BEACON_DETAIL_SAMPLE:
                return BeaconDetail.CONTENT_ITEM_TYPE;
            case SAMPLE_DETAIL:
                return SampleDetail.CONTENT_TYPE;*/
//            case SAMPLE_DETAIL_ID:
//                return SampleDetail.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Uri contentUri;
        String tableName;

        switch (sUriMatcher.match(uri)) {
            case ZONE:
                contentUri = Zone.CONTENT_URI;
                tableName = ZONE_TABLE_NAME;
                break;
            case WORKSPOT:
                contentUri = WorkSpot.CONTENT_URI;
                tableName = WORKSPOT_TABLE_NAME;
                break;
            case SAMPLESPOT:
                contentUri = SampleSpot.CONTENT_URI;
                tableName = SAMPLESPOT_TABLE_NAME;
                break;
            /*case SAMPLE:
                contentUri = Sample.CONTENT_URI;
                tableName = SAMPLE_TABLE_NAME;
                break;
            case BEACON_UUID:
                contentUri = BeaconUUID.CONTENT_URI;
                tableName = BEACON_UUID_TABLE_NAME;
                break;
            case BEACON_DETAIL:
                contentUri = BeaconDetail.CONTENT_URI;
                tableName = BEACON_TABLE_NAME;
                break;*/
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues validValues;
        if(values != null) {
            validValues = new ContentValues(values);
        } else {
            validValues = new ContentValues();
        }

//        if(validValues.containsKey(Samples.NAME) == false) {
//            throw new SQLException("Name must be specified");
//        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insertOrThrow(tableName, null, validValues);

        if(rowId > 0) { // 通知变更
            Uri resultUri = ContentUris.withAppendedId(contentUri, rowId);
            getContext().getContentResolver().notifyChange(resultUri, null);
            LogUtils.d("insert a row in " + tableName + " with id = " + rowId);
            return resultUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        // 支持级联删除和更新
        db.execSQL("PRAGMA foreign_keys = ON;");
        int count;
        switch (sUriMatcher.match(uri)) {
            case WORKSPOT:
                count = db.delete(WORKSPOT_TABLE_NAME, selection, selectionArgs);
                break;
            case WORKSPOT_ID:
                String workspotId = uri.getPathSegments().get(1);
                count = db.delete(WORKSPOT_TABLE_NAME, WorkSpot._ID + "=" + workspotId
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * 样本数据库
     */
    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            // 创建定位区域
            db.execSQL("CREATE TABLE " + ZONE_TABLE_NAME + " ("
                                       + Zone._ID + " INTEGER PRIMARY KEY,"
                                       + Zone.NAME + " TEXT"
                                       + ");");

            // 创建采集点表
            db.execSQL("CREATE TABLE " + WORKSPOT_TABLE_NAME + " ("
                                       + WorkSpot._ID + " INTEGER PRIMARY KEY,"
                                       + WorkSpot.X + " TEXT NOT NULL DEFAULT 0.00,"
                                       + WorkSpot.Y + " TEXT NOT NULL DEFAULT 0.00,"
                                       + WorkSpot.ZONE + " INTEGER NOT NULL REFERENCES " + ZONE_TABLE_NAME
                                                   + "(" + Zone._ID + ") ON UPDATE CASCADE"
                                       + ");");

            // 创建样本点表
            db.execSQL("CREATE TABLE " + SAMPLESPOT_TABLE_NAME + " ("
                    + SampleSpot._ID + " INTEGER PRIMARY KEY,"
                    + SampleSpot.X + " FLOAT NOT NULL DEFAULT 0.00,"
                    + SampleSpot.Y + " FLOAT NOT NULL DEFAULT 0.00,"
                    + SampleSpot.D + " FLOAT NOT NULL DEFAULT 0.00,"
                    + SampleSpot.COUNT + " INTEGER NOT NULL DEFAULT 0,"
                    + SampleSpot.STATUS + " INTEGER NOT NULL DEFAULT 1,"
                    + SampleSpot.WORKSPOT + " INTEGER NOT NULL REFERENCES " + WORKSPOT_TABLE_NAME
                    + "(" + WorkSpot._ID + ") ON UPDATE CASCADE ON DELETE CASCADE"
                    + ");");


            // 创建样本表
            /*db.execSQL("CREATE TABLE " + SAMPLE_TABLE_NAME + " ("
                                       + Sample._ID + " INTEGER PRIMARY KEY,"
                                       + Sample.NAME + " TEXT,"
                                       + Sample.TIME + " TEXT,"
                                       + Sample.SPOT + " INTEGER NOT NULL"
                                       + ");");*/

            // 创建样本表
            /*db.execSQL("CREATE TABLE " + BEACON_UUID_TABLE_NAME + " ("
                                       + BeaconUUID._ID + " INTEGER PRIMARY KEY,"
                                       + BeaconUUID.UUID + " TEXT NOT NULL UNIQUE"
                                       + ");");*/

            // 创建Beacon表
            /*db.execSQL("CREATE TABLE " + BEACON_TABLE_NAME + " ("
                                       + BeaconDetail._ID + " INTEGER PRIMARY KEY,"
                                       + BeaconDetail.UUID + " TEXT NOT NULL,"
                                       + BeaconDetail.MAJOR + " INTEGER DEFAULT 0,"
                                       + BeaconDetail.MINOR + " INTEGER DEFAULT 0,"
                                       + BeaconDetail.MAC + " TEXT NOT NULL,"
                                       + BeaconDetail.TIME + " TEXT NOT NULL,"
                                       + BeaconDetail.RSSI + " INTEGER DEFAULT 0,"
                                       + BeaconDetail.REMOTE_ID + " INTEGER DEFAULT 0,"
                                       + BeaconDetail.ACCURACY + " DECIMAL DEFAULT 0.00,"
                                       + BeaconDetail.DIRECTION + " DECIMAL DEFAULT 0.00,"
                                       + BeaconDetail.SAMPLE + " INTEGER NOT NULL"
                                       + ");");*/

            // 创建样本详情视图
//            db.execSQL("CREATE VIEW " + SAMPLE_DETAIL_VIEW_NAME + " AS SELECT "
//                                      + BeaconDetail.UUID + ", "
//                                      + BeaconDetail.MAJOR + ", "
//                                      + BeaconDetail.MINOR + ", "
//                                      + BeaconDetail.MAC + ", "
//                                      + BeaconDetail.RSSI + ", "
//                                      + BeaconDetail.ACCURACY + ", "
//                                      + BeaconDetail.DIRECTION + ", "
//                                      + SPOT_TABLE_NAME + "." + Spot.ZONE + ", "
//                                      + SPOT_TABLE_NAME + "." + Spot.X + ", "
//                                      + SPOT_TABLE_NAME + "." + Spot.Y + ", "
//                                      + SPOT_TABLE_NAME + "." + Spot.D + ", "
//                                      + SAMPLE_TABLE_NAME + "." + Sample.SPOT + ", "
//                                      + SAMPLE_TABLE_NAME + "." + Sample.TIME
//                                      + " FROM " + BEACON_TABLE_NAME
//                                      + " LEFT OUTER JOIN " + SAMPLE_TABLE_NAME + " ON "
//                                      + BEACON_TABLE_NAME + "." + BeaconDetail.SAMPLE + "="
//                                      + SAMPLE_TABLE_NAME + "." + Sample._ID
//                                      + " LEFT OUTER JOIN " + SPOT_TABLE_NAME + " ON "
//                                      + SAMPLE_TABLE_NAME + "." + Sample.SPOT + "="
//                                      + SPOT_TABLE_NAME + "." + Spot._ID
//                                      + " LEFT OUTER JOIN " + ZONE_TABLE_NAME + " ON "
//                                      + SPOT_TABLE_NAME + "." + Spot.ZONE + "="
//                                      + ZONE_TABLE_NAME + "." + Zone._ID
//                                      + ";");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
