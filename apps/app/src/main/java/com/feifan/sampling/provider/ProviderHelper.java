package com.feifan.sampling.provider;

import android.content.Context;

/**
 * Created by mengmeng on 16/6/29.
 */
public class ProviderHelper {
    public static void clearZoneMap(Context context){
        // 设置删除条件
        String where = SampleData.Zone.REMOTE_ID + ">?";
        String[] selectionArgs = { "0" };
        context.getContentResolver().delete(SampleData.Zone.CONTENT_URI,where,selectionArgs);
    }

    public static void clearSpotMap(Context context){
        // 设置删除条件
        String where = SampleData.Spot._ID + ">?";
        String[] selectionArgs = { "0" };
        context.getContentResolver().delete(SampleData.Spot.CONTENT_URI,where,selectionArgs);
    }

}
