package com.feifan.sampling.spot;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.feifan.sampling.provider.SampleData;

/**
 * Created by mengmeng on 16/6/29.
 */
public class SpotHelper {
    public static void saveRemoteId(Context context,final String x, final String y, final String d, final String zoneid, final String remoteid){
        if(!TextUtils.isEmpty(zoneid)){
            // 保存到数据库
            ContentValues values = new ContentValues();
            values.put(SampleData.Spot.X, x);
            values.put(SampleData.Spot.Y, y);
            values.put(SampleData.Spot.D, d);
            values.put(SampleData.Spot.NAME, x+"_"+y+"_"+d+"_"+zoneid);
            values.put(SampleData.Spot.ZONE, zoneid);
            values.put(SampleData.Spot.REMOTE_ID, remoteid);
            Uri spotUri = context.getContentResolver().insert(SampleData.Spot.CONTENT_URI, values);
        }
    }
}
