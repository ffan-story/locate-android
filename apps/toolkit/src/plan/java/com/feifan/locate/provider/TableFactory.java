package com.feifan.locate.provider;

import android.database.sqlite.SQLiteDatabase;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.Constants;
import com.feifan.locate.provider.LocateData.Building;
import com.feifan.locate.provider.LocateData.LineSpot;
import com.feifan.locate.provider.LocateData.Mac;
import com.feifan.locate.provider.LocateData.WorkLine;

/**
 * Created by xuchunlei on 16/10/14.
 */

public final class TableFactory {

    // 数据库表名－定位建筑
    static final String BUILDING_TABLE_NAME = "building";
    // 数据库表名－定位区域
    static final String ZONE_TABLE_NAME = "zone";
    // 数据库表名－采集点
    static final String WORKSPOT_TABLE_NAME = "workspot";
    // 数据库表名－样本点
    static final String SAMPLESPOT_TABLE_NAME = "samplespot";
    // 数据库表名-采集路线点
    static final String LINESPOT_TABLE_NAME = "linespot";
    // 数据库表名-采集路线
    static final String WORKLINE_TABLE_NAME = "workline";
    // 数据库表名-样本路线
    static final String SAMPLELINE_TABLE_NAME = "sampleline";
    // 数据库表名-Mac地址映射
    static final String MAC_TABLE_NAME = "mac";

    private TableFactory() {

    }

    /**
     * 创建定位建筑表
     * @param db
     */
    public static void createBuildingTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + BUILDING_TABLE_NAME + " ("
                + Building._ID + " INTEGER PRIMARY KEY,"
                + Building.NAME + " TEXT,"
                + Building.BUILDING_ID + " TEXT NOT NULL UNIQUE,"
                + Building.MIN_FLOOR + " INTEGER DEFAULT 0"
                + ");");
        LogUtils.v(BUILDING_TABLE_NAME + " table created");
    }

    /**
     * 创建定位区域表
     * @param db
     */
    public static void createZoneTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ZONE_TABLE_NAME + " ("
                + LocateData.Zone._ID + " INTEGER PRIMARY KEY,"
                + LocateData.Zone.NAME + " TEXT,"
                + LocateData.Zone.PLAN + " TEXT,"
                + LocateData.Zone.SCALE + " FLOAT NOT NULL DEFAULT 1.00,"
                + LocateData.Zone.FLOOR_NO + " INTEGER NOT NULL DEFAULT 0,"
                + LocateData.Zone.TITLE + " TEXT,"
                + LocateData.Zone.BUILDING + " INTEGER NOT NULL REFERENCES " + BUILDING_TABLE_NAME
                + "(" + Building._ID + ")"
                + ");");
        LogUtils.v(ZONE_TABLE_NAME + " table created");
    }

    /**
     * 创建采集点表
     * @param db
     */
    public static void createWorkSpotTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + WORKSPOT_TABLE_NAME + " ("
                + LocateData.WorkSpot._ID + " INTEGER PRIMARY KEY,"
                + LocateData.WorkSpot.X + " TEXT NOT NULL DEFAULT 0.00,"
                + LocateData.WorkSpot.Y + " TEXT NOT NULL DEFAULT 0.00,"
                + LocateData.WorkSpot.MOVABLE + " BOOLEAN NOT NULL DEFAULT TRUE,"
                + LocateData.WorkSpot.ZONE + " INTEGER NOT NULL REFERENCES " + ZONE_TABLE_NAME
                + "(" + LocateData.Zone._ID + ") ON UPDATE CASCADE"
                + ");");
        LogUtils.v(WORKSPOT_TABLE_NAME + " table created");
    }

    /**
     * 创建样本点表
     * @param db
     */
    public static void createSampleSpotTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + SAMPLESPOT_TABLE_NAME + " ("
                + LocateData.SampleSpot._ID + " INTEGER PRIMARY KEY,"
                + LocateData.SampleSpot.X + " FLOAT NOT NULL DEFAULT 0.00,"
                + LocateData.SampleSpot.Y + " FLOAT NOT NULL DEFAULT 0.00,"
                + LocateData.SampleSpot.D + " FLOAT NOT NULL DEFAULT 0.00,"
                + LocateData.SampleSpot.COUNT + " INTEGER NOT NULL DEFAULT 0,"
                + LocateData.SampleSpot.TOTAL + " INTEGER NOT NULL DEFAULT " + Constants.SCAN_DEFAULT_TIMES + ","
                + LocateData.SampleSpot.TIMES + " INTEGER NOT NULL DEFAULT 0,"
                + LocateData.SampleSpot.STATUS + " INTEGER NOT NULL DEFAULT 1,"
                + LocateData.SampleSpot.WORKSPOT + " INTEGER NOT NULL REFERENCES " + WORKSPOT_TABLE_NAME
                + "(" + LocateData.WorkSpot._ID + ") ON UPDATE CASCADE ON DELETE CASCADE"
                + ");");
        LogUtils.v(SAMPLESPOT_TABLE_NAME + " table created");
    }

    /**
     * 创建采集路线点表
     * @param db
     */
    public static void createLineSpotTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + LINESPOT_TABLE_NAME + " ("
                + LineSpot._ID + " INTEGER PRIMARY KEY,"
                + LineSpot.X + " TEXT NOT NULL DEFAULT 0.00,"
                + LineSpot.Y + " TEXT NOT NULL DEFAULT 0.00,"
                + LineSpot.MOVABLE + " BOOLEAN NOT NULL DEFAULT TRUE,"
                + LineSpot.ZONE + " INTEGER NOT NULL REFERENCES " + ZONE_TABLE_NAME
                + "(" + LocateData.Zone._ID + ") ON UPDATE CASCADE"
                + ");");
        LogUtils.v(LINESPOT_TABLE_NAME + " table created");
    }

    /**
     * 创建采集路线表
     * @param db
     */
    public static void createWorkLineTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + WORKLINE_TABLE_NAME + " ("
                + WorkLine._ID + " INTEGER PRIMARY KEY,"
                + WorkLine.SPOT_ONE + " INTEGER NOT NULL REFERENCES " + LINESPOT_TABLE_NAME
                + "(" + LineSpot._ID + ") ON DELETE CASCADE,"
                + WorkLine.SPOT_TWO + " INTEGER NOT NULL REFERENCES " + LINESPOT_TABLE_NAME
                + "(" + LineSpot._ID + ") ON DELETE CASCADE,"
                + WorkLine.ZONE + " INTEGER NOT NULL REFERENCES " + ZONE_TABLE_NAME
                + "(" + LocateData.Zone._ID + ") ON UPDATE CASCADE"
                + ");");
        LogUtils.v(WORKLINE_TABLE_NAME + " table created");
    }

    /**
     * 创建样本路线表
     * @param db
     */
    public static void createSampleLineTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + SAMPLELINE_TABLE_NAME + " ("
                + LocateData.SampleLine._ID + " INTEGER PRIMARY KEY,"
                + LocateData.SampleLine._NAME + " TEXT,"
                + LocateData.SampleLine._TOTAL + " INTEGER NOT NULL DEFAULT 0,"
                + LocateData.SampleLine._PROGRESS + " TEXT DEFAULT '0.00',"
                + LocateData.SampleLine._STATUS + " INTEGER NOT NULL DEFAULT 1,"
                + LocateData.SampleLine.D + " FLOAT NOT NULL DEFAULT 0.00,"
                + LocateData.SampleLine.WORKLINE + " INTEGER NOT NULL REFERENCES " + WORKLINE_TABLE_NAME
                + "(" + WorkLine._ID + ") ON UPDATE CASCADE ON DELETE CASCADE"
                + ");");
        LogUtils.v(SAMPLELINE_TABLE_NAME + " table created");
    }

    /**
     * 创建Mac地址表
     * @param db
     */
    public static void createMacTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MAC_TABLE_NAME + " ("
                + Mac._ID + " INTEGER PRIMARY KEY,"
                + Mac.ADDRESS + " TEXT NOT NULL UNIQUE,"
                + Mac.UUID + " TEXT NOT NULL,"
                + Mac.MAJOR + " INTEGER NOT NULL DEFAULT 0,"
                + Mac.MINOR + " INTEGER NOT NULL DEFAULT 0,"
                + Mac.PLAZA + " TEXT NOT NULL,"
                + Mac.BUILDING + " TEXT NOT NULL REFERENCES " + BUILDING_TABLE_NAME
                + "(" + Building.BUILDING_ID + ")"
                + ");");
        LogUtils.v(MAC_TABLE_NAME + " table created");
    }

    public static String getWorkLineQueryTable() {

//        return WORKLINE_TABLE_NAME + "," + LINESPOT_TABLE_NAME + " as spotOne" + ","
//                + LINESPOT_TABLE_NAME + " as spotTwo where "
//                + WORKLINE_TABLE_NAME + "." + WorkLine.SPOT_ONE + "=spotOne." + LineSpot._ID + " and "
//                + WORKLINE_TABLE_NAME + "." + WorkLine.SPOT_TWO + "=spotTwo." + LineSpot._ID;

        return WORKLINE_TABLE_NAME + "," + LINESPOT_TABLE_NAME + " as spotOne" + ","
                + LINESPOT_TABLE_NAME + " as spotTwo";
    }

    public static String getZoneQueryTable() {
        return ZONE_TABLE_NAME + "," + BUILDING_TABLE_NAME;
    }
}
