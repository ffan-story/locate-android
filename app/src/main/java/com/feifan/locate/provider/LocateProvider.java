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
import com.feifan.locate.Constants;
import com.feifan.locate.provider.LocateData.Building;
import com.feifan.locate.provider.LocateData.Zone;
import com.feifan.locate.provider.LocateData.WorkSpot;
import com.feifan.locate.provider.LocateData.SampleSpot;
import com.feifan.locate.provider.LocateData.WorkLine;
import com.feifan.locate.provider.LocateData.SampleLine;
import com.feifan.locate.provider.LocateData.LineSpot;

import static com.feifan.locate.provider.TableFactory.WORKLINE_TABLE_NAME;
//import com.feifan.locate.provider.LocateData.Mac;

/**
 * Created by xuchunlei on 16/4/21.
 */
public class LocateProvider extends ContentProvider {

    private static final String AUTHORITY = LocateData.AUTHORITY;

    // 数据库名称
    private static final String DATABASE_NAME = "sample.db";
    // 数据库版本
    private static final int DATABASE_VERSION = 1;

    // 数据库表名-Mac地址映射
    private static final String MAC_TABLE_NAME = "mac";

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
    private static final int BUILDING = 1;
    private static final int BUILDING_ID = BUILDING + 1;
    private static final int ZONE = BUILDING_ID + 1;
    private static final int ZONE_ID = ZONE + 1;
    private static final int WORKSPOT = ZONE_ID + 1;
    private static final int WORKSPOT_ID = WORKSPOT + 1;
    private static final int SAMPLESPOT = WORKSPOT_ID + 1;
    private static final int SAMPLESPOT_ID = SAMPLESPOT + 1;
    private static final int LINESPOT = SAMPLESPOT_ID + 1;
    private static final int LINESPOT_ID = LINESPOT + 1;
    private static final int WORKLINE = LINESPOT_ID + 1;
    private static final int WORKLINE_ID = WORKLINE + 1;
    private static final int SAMPLELINE = WORKLINE_ID + 1;
    private static final int SAMPLELINE_ID = SAMPLELINE + 1;

/*
    private static final int MAC = SAMPLESPOT_ID + 1;
    private static final int MAC_ID = MAC + 1;
    private static final int SAMPLE = SPOT_ID + 1;
    private static final int SAMPLE_ID = SAMPLE + 1;
    private static final int BEACON_UUID = SAMPLE_ID + 1;
    private static final int BEACON_UUID_ID = BEACON_UUID + 1;
    private static final int BEACON_DETAIL = BEACON_UUID_ID + 1;
    private static final int BEACON_DETAIL_ID = BEACON_DETAIL + 1;
    private static final int BEACON_DETAIL_SAMPLE = BEACON_DETAIL_ID + 1; // 外键－样本
    private static final int SAMPLE_DETAIL = BEACON_DETAIL_SAMPLE + 1;*/
//    private static final int SAMPLE_DETAIL_ID = SAMPLE_DETAIL + 1;

    // 定位建筑表映射集合
    private static HashMap<String, String> sBuildingProjectionMap;
    // 定位区域表列映射集合
    private static HashMap<String, String> sZoneProjectionMap;
    // 采集点表列映射集合
    private static HashMap<String, String> sWorkSpotProjectionMap;
    // 样本点表列映射集合
    private static HashMap<String, String> sSampleSpotProjectionMap;
    // 采集路线点表映射集合
    private static HashMap<String, String> sLineSpotProjectionMap;
    // 采集路线表映射集合
    private static HashMap<String, String> sWorkLineProjectionMap;
    // 样本路线表映射集合
    private static HashMap<String, String> sSampleLineProjectionMap;

    // Mac地址表映射集合
//    private static HashMap<String, String> sMacProjectionMap;

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
        sUriMatcher.addURI(AUTHORITY, "building", BUILDING);
        sUriMatcher.addURI(AUTHORITY, "building/#", BUILDING_ID);
        sUriMatcher.addURI(AUTHORITY, "zone", ZONE);
        sUriMatcher.addURI(AUTHORITY, "zone/#", ZONE_ID);
        sUriMatcher.addURI(AUTHORITY, "workspot", WORKSPOT);
        sUriMatcher.addURI(AUTHORITY, "workspot/#", WORKSPOT_ID);
        sUriMatcher.addURI(AUTHORITY, "samplespot", SAMPLESPOT);
        sUriMatcher.addURI(AUTHORITY, "samplespot/#", SAMPLESPOT_ID);
        sUriMatcher.addURI(AUTHORITY, "linespot", LINESPOT);
        sUriMatcher.addURI(AUTHORITY, "linespot/#", LINESPOT_ID);
        sUriMatcher.addURI(AUTHORITY, "workline", WORKLINE);
        sUriMatcher.addURI(AUTHORITY, "workline/#", WORKLINE_ID);
        sUriMatcher.addURI(AUTHORITY, "sampleline", SAMPLELINE);
        sUriMatcher.addURI(AUTHORITY, "sampleline/#", SAMPLELINE_ID);
        /*
        sUriMatcher.addURI(AUTHORITY, "mac", MAC);
        sUriMatcher.addURI(AUTHORITY, "mac/#", MAC_ID);
        sUriMatcher.addURI(AUTHORITY, "sample", SAMPLE);
        sUriMatcher.addURI(AUTHORITY, "sample/#", SAMPLE_ID);
        sUriMatcher.addURI(AUTHORITY, "beacon_uuid", BEACON_UUID);
        sUriMatcher.addURI(AUTHORITY, "beacon_uuid/#", BEACON_UUID_ID);
        sUriMatcher.addURI(AUTHORITY, "beacon", BEACON_DETAIL);
        sUriMatcher.addURI(AUTHORITY, "beacon/#", BEACON_DETAIL_ID);
        sUriMatcher.addURI(AUTHORITY, "beacon/sample/#", BEACON_DETAIL_SAMPLE);
        sUriMatcher.addURI(AUTHORITY, "sample_detail", SAMPLE_DETAIL);*/

        sBuildingProjectionMap = new HashMap<>();
        sBuildingProjectionMap.put(Building._ID, Building._ID);
        sBuildingProjectionMap.put(Building.NAME, Building.NAME);
        sBuildingProjectionMap.put(Building.CODE, Building.CODE);
        sBuildingProjectionMap.put(Building.MIN_FLOOR, Building.MIN_FLOOR);

        sZoneProjectionMap = new HashMap<>();
        sZoneProjectionMap.put(Zone._ID, Zone._ID);
        sZoneProjectionMap.put(Zone.NAME, Zone.NAME);
        sZoneProjectionMap.put(Zone.PLAN, Zone.PLAN);
        sZoneProjectionMap.put(Zone.SCALE, Zone.SCALE);
        sZoneProjectionMap.put(Zone.FLOOR_NO, Zone.FLOOR_NO);
        sZoneProjectionMap.put(Zone.TITLE, Zone.TITLE);

        sWorkSpotProjectionMap = new HashMap<>();
        sWorkSpotProjectionMap.put(WorkSpot._ID, WorkSpot._ID);
        sWorkSpotProjectionMap.put(WorkSpot.X, WorkSpot.X);
        sWorkSpotProjectionMap.put(WorkSpot.Y, WorkSpot.Y);
        sWorkSpotProjectionMap.put(WorkSpot.MOVABLE, WorkSpot.MOVABLE);
        sWorkSpotProjectionMap.put(WorkSpot.ZONE, WorkSpot.ZONE);

        sSampleSpotProjectionMap = new HashMap<>();
        sSampleSpotProjectionMap.put(SampleSpot._ID, SampleSpot._ID);
        sSampleSpotProjectionMap.put(SampleSpot.X, SampleSpot.X);
        sSampleSpotProjectionMap.put(SampleSpot.Y, SampleSpot.Y);
        sSampleSpotProjectionMap.put(SampleSpot.D, SampleSpot.D);
        sSampleSpotProjectionMap.put(SampleSpot.STATUS, SampleSpot.STATUS);
        sSampleSpotProjectionMap.put(SampleSpot.COUNT, SampleSpot.COUNT);
        sSampleSpotProjectionMap.put(SampleSpot.TOTAL, SampleSpot.TOTAL);
        sSampleSpotProjectionMap.put(SampleSpot.TIMES, SampleSpot.TIMES);

        sLineSpotProjectionMap = new HashMap<>();
        sLineSpotProjectionMap.put(LineSpot._ID, LineSpot._ID);
        sLineSpotProjectionMap.put(LineSpot.X, LineSpot.X);
        sLineSpotProjectionMap.put(LineSpot.Y, LineSpot.Y);
        sLineSpotProjectionMap.put(LineSpot.MOVABLE, LineSpot.MOVABLE);
        sLineSpotProjectionMap.put(LineSpot.ZONE, LineSpot.ZONE);

        sWorkLineProjectionMap = new HashMap<>();
        sWorkLineProjectionMap.put(
                WORKLINE_TABLE_NAME + "." + WorkLine._ID,
                WORKLINE_TABLE_NAME + "." + WorkLine._ID + " AS " + WorkLine._ID);
        sWorkLineProjectionMap.put(WorkLine.SPOT_ONE, WorkLine.SPOT_ONE + " AS one_id");
        sWorkLineProjectionMap.put("spotOne." + LineSpot.X, "spotOne." + LineSpot.X + " AS one_x");
        sWorkLineProjectionMap.put("spotOne." + LineSpot.Y, "spotOne." + LineSpot.Y + " AS one_y");
        sWorkLineProjectionMap.put(WorkLine.SPOT_TWO, WorkLine.SPOT_TWO + " AS two_id");
        sWorkLineProjectionMap.put("spotTwo." + LineSpot.X, "spotTwo." + LineSpot.X + " AS two_x");
        sWorkLineProjectionMap.put("spotTwo." + LineSpot.Y, "spotTwo." + LineSpot.Y + " AS two_y");
        sWorkLineProjectionMap.put(
                WORKLINE_TABLE_NAME + "." + WorkLine.ZONE,
                WORKLINE_TABLE_NAME + "." + WorkLine.ZONE + " AS " + WorkLine.ZONE);

        sSampleLineProjectionMap = new HashMap<>();
        sSampleLineProjectionMap.put(SampleLine._ID, SampleLine._ID);
        sSampleLineProjectionMap.put(SampleLine._NAME, SampleLine._NAME);
        sSampleLineProjectionMap.put(SampleLine._TOTAL, SampleLine._TOTAL);
        sSampleLineProjectionMap.put(SampleLine._PROGRESS, SampleLine._PROGRESS);
        sSampleLineProjectionMap.put(SampleLine._STATUS, SampleLine._STATUS);
        sSampleLineProjectionMap.put(SampleLine.D, SampleLine.D);
        sSampleLineProjectionMap.put(SampleLine.WORKLINE, SampleLine.WORKLINE);

//        sMacProjectionMap = new HashMap<>();
//        sMacProjectionMap.put(Mac._ID, Mac._ID);
//        sMacProjectionMap.put(Mac.ADDRESS, Mac.ADDRESS);
//        sMacProjectionMap.put(Mac.UUID, Mac.UUID);
//        sMacProjectionMap.put(Mac.MAJOR, Mac.MAJOR);
//        sMacProjectionMap.put(Mac.MINOR, Mac.MINOR);

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
            case BUILDING:
                qb.setTables(TableFactory.BUILDING_TABLE_NAME);
                qb.setProjectionMap(sBuildingProjectionMap);
                break;
            case ZONE:          // 查询定位区域整表
                qb.setTables(TableFactory.ZONE_TABLE_NAME);
                qb.setProjectionMap(sZoneProjectionMap);
                break;
            case WORKSPOT:
                qb.setTables(TableFactory.WORKSPOT_TABLE_NAME);
                qb.setProjectionMap(sWorkSpotProjectionMap);
                break;
            case SAMPLESPOT:
                qb.setTables(TableFactory.SAMPLESPOT_TABLE_NAME);
                qb.setProjectionMap(sSampleSpotProjectionMap);
                break;
            case LINESPOT:
                qb.setTables(TableFactory.LINESPOT_TABLE_NAME);
                qb.setProjectionMap(sLineSpotProjectionMap);
                break;
            case WORKLINE:
                qb.setTables(TableFactory.getWorkLineQueryTable());
                qb.setProjectionMap(sWorkLineProjectionMap);
                qb.appendWhere(WORKLINE_TABLE_NAME + "." + WorkLine.SPOT_ONE + "=spotOne." + LineSpot._ID);
                qb.appendWhere(" and ");
                qb.appendWhere(WORKLINE_TABLE_NAME + "." + WorkLine.SPOT_TWO + "=spotTwo." + LineSpot._ID);
                break;
            case SAMPLELINE:
                qb.setTables(TableFactory.SAMPLELINE_TABLE_NAME);
                qb.setProjectionMap(sSampleLineProjectionMap);
                break;
//            case MAC:
//                qb.setTables(MAC_TABLE_NAME);
//                qb.setProjectionMap(sMacProjectionMap);
//                break;
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
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        // 设置监听游标的uri，当数据变更时通过该uri可监控到
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case BUILDING:
                return Building.CONTENT_TYPE;
            case BUILDING_ID:
                return Building.CONTENT_ITEM_TYPE;
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
            case LINESPOT:
                return LineSpot.CONTENT_TYPE;
            case LINESPOT_ID:
                return LineSpot.CONTENT_ITEM_TYPE;
            case WORKLINE:
                return WorkLine.CONTENT_TYPE;
            case WORKLINE_ID:
                return WorkLine.CONTENT_ITEM_TYPE;
            case SAMPLELINE:
                return SampleLine.CONTENT_TYPE;
            case SAMPLELINE_ID:
                return SampleLine.CONTENT_ITEM_TYPE;
//            case MAC:
//                return Mac.CONTENT_TYPE;
//            case MAC_ID:
//                return Mac.CONTENT_ITEM_TYPE;
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
            case BUILDING:
                contentUri = Building.CONTENT_URI;
                tableName = TableFactory.BUILDING_TABLE_NAME;
                break;
            case ZONE:
                contentUri = Zone.CONTENT_URI;
                tableName = TableFactory.ZONE_TABLE_NAME;
                break;
            case WORKSPOT:
                contentUri = WorkSpot.CONTENT_URI;
                tableName = TableFactory.WORKSPOT_TABLE_NAME;
                break;
            case SAMPLESPOT:
                contentUri = SampleSpot.CONTENT_URI;
                tableName = TableFactory.SAMPLESPOT_TABLE_NAME;
                break;
            case LINESPOT:
                contentUri = LineSpot.CONTENT_URI;
                tableName = TableFactory.LINESPOT_TABLE_NAME;
                break;
            case WORKLINE:
                contentUri = WorkLine.CONTENT_URI;
                tableName = WORKLINE_TABLE_NAME;
                break;
            case SAMPLELINE:
                contentUri = SampleLine.CONTENT_URI;
                tableName = TableFactory.SAMPLELINE_TABLE_NAME;
                break;
//            case MAC:
//                contentUri = Mac.CONTENT_URI;
//                tableName = MAC_TABLE_NAME;
//                break;
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
                count = db.delete(TableFactory.WORKSPOT_TABLE_NAME, selection, selectionArgs);
                break;
            case WORKSPOT_ID:
                String workspotId = uri.getPathSegments().get(1);
                count = db.delete(TableFactory.WORKSPOT_TABLE_NAME, WorkSpot._ID + "=" + workspotId
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case LINESPOT_ID:
                String linespotId = uri.getPathSegments().get(1);
                count = db.delete(TableFactory.LINESPOT_TABLE_NAME, LineSpot._ID + "=" + linespotId
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
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case WORKSPOT:
                count = db.update(TableFactory.WORKSPOT_TABLE_NAME, values, selection, selectionArgs);
                break;
            case SAMPLESPOT:
                count = db.update(TableFactory.SAMPLESPOT_TABLE_NAME, values, selection, selectionArgs);
                break;
            case SAMPLELINE:
                count = db.update(TableFactory.SAMPLELINE_TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                count = 0;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
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
            TableFactory.createBuildingTable(db);

            // 创建定位区域
            TableFactory.createZoneTable(db);

            // 创建采集点表
            TableFactory.createWorkSpotTable(db);

            // 创建样本点表
            TableFactory.createSampleSpotTable(db);

            // 创建采集路线点表
            TableFactory.createLineSpotTable(db);

            // 创建采集路线表
            TableFactory.createWorkLineTable(db);

            // 创建样本路线表
            TableFactory.createSampleLineTable(db);

            // 创建Mac地址映射表
//            db.execSQL("CREATE TABLE " + MAC_TABLE_NAME + " ("
//                    + Mac._ID + " INTEGER PRIMARY KEY,"
//                    + Mac.ADDRESS + " TEXT NOT NULL,"
//                    + Mac.UUID + " TEXT NOT NULL,"
//                    + Mac.MAJOR + " INTEGER NOT NULL DEFAULT 0,"
//                    + Mac.MINOR + " INTEGER NOT NULL DEFAULT 0,"
//                    + Mac.BUILDING + " TEXT NOT NULL REFERENCES " + BUILDING_TABLE_NAME
//                    + "(" + Building.CODE + ")"
//                    + ");");

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
