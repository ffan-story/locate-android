package com.feifan.locate;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Build;
import android.util.Log;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.provider.LocateData;

import java.util.HashMap;
import java.util.Map;
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
        values.put(LocateData.Building.CODE, "860100010060300001");
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
        values.put(LocateData.Zone.SCALE, getScale(2));
        values.put(LocateData.Zone.FLOOR_NO, 1);
        values.put(LocateData.Zone.TITLE, "F1");
        values.put(LocateData.Zone.BUILDING, 3);
        resolver.insert(LocateData.Zone.CONTENT_URI, values);

        values.clear();
        values.put(LocateData.Zone._ID, 3);
        values.put(LocateData.Zone.NAME, "石景山万达广场F2");
        values.put(LocateData.Zone.PLAN, "zone_shijingshan_f2.jpg");
        values.put(LocateData.Zone.SCALE, getScale(3));
        values.put(LocateData.Zone.FLOOR_NO, 2);
        values.put(LocateData.Zone.TITLE, "F2");
        values.put(LocateData.Zone.BUILDING, 3);
        resolver.insert(LocateData.Zone.CONTENT_URI, values);

        values.clear();
        values.put(LocateData.Zone._ID, 4);
        values.put(LocateData.Zone.NAME, "石景山万达广场F3");
        values.put(LocateData.Zone.PLAN, "zone_shijingshan_f3.jpg");
        values.put(LocateData.Zone.SCALE, getScale(4));
        values.put(LocateData.Zone.FLOOR_NO, 3);
        values.put(LocateData.Zone.TITLE, "F3");
        values.put(LocateData.Zone.BUILDING, 3);
        resolver.insert(LocateData.Zone.CONTENT_URI, values);

        values.clear();
        values.put(LocateData.Zone._ID, 5);
        values.put(LocateData.Zone.NAME, "石景山万达广场B1");
        values.put(LocateData.Zone.PLAN, "zone_shijingshan_b1.jpg");
        values.put(LocateData.Zone.SCALE, getScale(5));
        values.put(LocateData.Zone.FLOOR_NO, -1);
        values.put(LocateData.Zone.TITLE, "B1");
        values.put(LocateData.Zone.BUILDING, 3);
        resolver.insert(LocateData.Zone.CONTENT_URI, values);

        values.clear();
        values.put(LocateData.Zone._ID, 6);
        values.put(LocateData.Zone.NAME, "石景山万达广场B2");
        values.put(LocateData.Zone.PLAN, "zone_shijingshan_b2.jpg");
        values.put(LocateData.Zone.SCALE, getScale(6));
        values.put(LocateData.Zone.FLOOR_NO, -2);
        values.put(LocateData.Zone.TITLE, "B2");
        values.put(LocateData.Zone.BUILDING, 3);
        resolver.insert(LocateData.Zone.CONTENT_URI, values);
    }

    public static Map<String, Float> TENSOR_DATA = new HashMap<>();
    static {
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42513", -91f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42514", -79f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42516", -91f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42559", -89.5f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42567", -78f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42614", -90f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42651", -85.5f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42793", -86f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42857", -69.5f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_42999", -90f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43015", -85f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43096", -75f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43124", -84f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43131", -88f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43152", -73f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43157", -76f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43202", -90f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43228", -89f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43255", -89f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43296", -79.5f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43303", -82f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43328", -90f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43443", -91f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43482", -83.5f);
        TENSOR_DATA.put("A3FCE438-627C-42B7-AB72-DC6E55E137AC_11000_43515", -86f);
    }

    private static float getScale(int id) {
        switch (id) {
            case 2:
            case 3:
            case 4:
                if(Build.MANUFACTURER.contains("Meizu")) {
                    return 0.136f * 1.333f;
                }
                return 0.136f;
            case 5:
            case 6:
                if(Build.MANUFACTURER.contains("Meizu")) {
                    return 0.226f * 1.333f;
                }
                return 0.226f;
            default:
                return 1f;
        }

    }
}
