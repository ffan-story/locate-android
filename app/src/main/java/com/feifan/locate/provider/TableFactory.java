package com.feifan.locate.provider;

import android.database.sqlite.SQLiteDatabase;

import com.feifan.locate.Constants;
import com.feifan.locate.provider.LocateData.WorkLine;
import com.feifan.locate.provider.LocateData.LineSpot;

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

    private TableFactory() {

    }

    /**
     * 创建定位建筑表
     * @param db
     */
    public static void createBuildingTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + BUILDING_TABLE_NAME + " ("
                + LocateData.Building._ID + " INTEGER PRIMARY KEY,"
                + LocateData.Building.NAME + " TEXT,"
                + LocateData.Building.CODE + " TEXT NOT NULL UNIQUE,"
                + LocateData.Building.MIN_FLOOR + " INTEGER DEFAULT 0"
                + ");");
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
                + "(" + LocateData.Building._ID + ")"
                + ");");
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
                + LocateData.WorkLine.ZONE + " INTEGER NOT NULL REFERENCES " + ZONE_TABLE_NAME
                + "(" + LocateData.Zone._ID + ") ON UPDATE CASCADE"
                + ");");
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
                + "(" + LocateData.WorkLine._ID + ") ON UPDATE CASCADE ON DELETE CASCADE"
                + ");");
    }

    public static String getWorkLineQueryTable() {

//        return WORKLINE_TABLE_NAME + "," + LINESPOT_TABLE_NAME + " as spotOne" + ","
//                + LINESPOT_TABLE_NAME + " as spotTwo where "
//                + WORKLINE_TABLE_NAME + "." + WorkLine.SPOT_ONE + "=spotOne." + LineSpot._ID + " and "
//                + WORKLINE_TABLE_NAME + "." + WorkLine.SPOT_TWO + "=spotTwo." + LineSpot._ID;

        return WORKLINE_TABLE_NAME + "," + LINESPOT_TABLE_NAME + " as spotOne" + ","
                + LINESPOT_TABLE_NAME + " as spotTwo";
    }
}
