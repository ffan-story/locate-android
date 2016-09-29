package com.feifan.locate;

import android.content.ContentResolver;
import android.content.ContentValues;

import com.feifan.locate.provider.LocateData;

import java.util.concurrent.Executors;

/**
 * Created by xuchunlei on 16/9/19.
 */
public class MockServer {

    private MockServer(){

    }

    public static void requestBuildingData(ContentResolver resolver) {
        ContentValues values = new ContentValues();
        values.put(LocateData.Building._ID, 1);
        values.put(LocateData.Building.NAME, "金地中心A座");
        values.put(LocateData.Building.CODE, "A22");
        values.put(LocateData.Building.MIN_FLOOR, 22);
        resolver.insert(LocateData.Building.CONTENT_URI, values);

        values.clear();
        values.put(LocateData.Building._ID, 2);
        values.put(LocateData.Building.NAME, "金地中心B座");
        values.put(LocateData.Building.CODE, "B31");
        values.put(LocateData.Building.MIN_FLOOR, 31);
        resolver.insert(LocateData.Building.CONTENT_URI, values);

        values.clear();
        values.put(LocateData.Building._ID, 3);
        values.put(LocateData.Building.NAME, "石景山万达广场");
        values.put(LocateData.Building.CODE, "android_860100010060300001");
        values.put(LocateData.Building.MIN_FLOOR, -2);
        resolver.insert(LocateData.Building.CONTENT_URI, values);
    }

    public static void requestZoneData(ContentResolver resolver) {

        ContentValues values = new ContentValues();
        values.put(LocateData.Zone._ID, 1);
        values.put(LocateData.Zone.NAME, "金地中心B座31层");
        values.put(LocateData.Zone.PLAN, "zone_jindi_b31.png");
        values.put(LocateData.Zone.SCALE, 0.033);
        values.put(LocateData.Zone.FLOOR_NO, 31);
        values.put(LocateData.Zone.TITLE, "F31");
        values.put(LocateData.Zone.BUILDING, 2);
        resolver.insert(LocateData.Zone.CONTENT_URI, values);

        values.clear();
        values.put(LocateData.Zone._ID, 2);
        values.put(LocateData.Zone.NAME, "石景山万达广场F1");
        values.put(LocateData.Zone.PLAN, "zone_shijingshan_f1.jpg");
        values.put(LocateData.Zone.SCALE, 0.136);
        values.put(LocateData.Zone.FLOOR_NO, 1);
        values.put(LocateData.Zone.TITLE, "F1");
        values.put(LocateData.Zone.BUILDING, 3);
        resolver.insert(LocateData.Zone.CONTENT_URI, values);

        values.clear();
        values.put(LocateData.Zone._ID, 3);
        values.put(LocateData.Zone.NAME, "石景山万达广场F2");
        values.put(LocateData.Zone.PLAN, "zone_shijingshan_f2.jpg");
        values.put(LocateData.Zone.SCALE, 0.136);
        values.put(LocateData.Zone.FLOOR_NO, 2);
        values.put(LocateData.Zone.TITLE, "F2");
        values.put(LocateData.Zone.BUILDING, 3);
        resolver.insert(LocateData.Zone.CONTENT_URI, values);

        values.clear();
        values.put(LocateData.Zone._ID, 4);
        values.put(LocateData.Zone.NAME, "石景山万达广场F3");
        values.put(LocateData.Zone.PLAN, "zone_shijingshan_f3.jpg");
        values.put(LocateData.Zone.SCALE, 0.136);
        values.put(LocateData.Zone.FLOOR_NO, 3);
        values.put(LocateData.Zone.TITLE, "F3");
        values.put(LocateData.Zone.BUILDING, 3);
        resolver.insert(LocateData.Zone.CONTENT_URI, values);

        values.clear();
        values.put(LocateData.Zone._ID, 5);
        values.put(LocateData.Zone.NAME, "石景山万达广场B1");
        values.put(LocateData.Zone.PLAN, "zone_shijingshan_b1.jpg");
        values.put(LocateData.Zone.SCALE, 0.226);
        values.put(LocateData.Zone.FLOOR_NO, -1);
        values.put(LocateData.Zone.TITLE, "B1");
        values.put(LocateData.Zone.BUILDING, 3);
        resolver.insert(LocateData.Zone.CONTENT_URI, values);

        values.clear();
        values.put(LocateData.Zone._ID, 6);
        values.put(LocateData.Zone.NAME, "石景山万达广场B2");
        values.put(LocateData.Zone.PLAN, "zone_shijingshan_b2.jpg");
        values.put(LocateData.Zone.SCALE, 0.226);
        values.put(LocateData.Zone.FLOOR_NO, -2);
        values.put(LocateData.Zone.TITLE, "B2");
        values.put(LocateData.Zone.BUILDING, 3);
        resolver.insert(LocateData.Zone.CONTENT_URI, values);
    }
}
