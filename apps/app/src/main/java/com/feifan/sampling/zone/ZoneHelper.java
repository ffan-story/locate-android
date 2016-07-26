package com.feifan.sampling.zone;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.feifan.sampling.Constants;
import com.feifan.sampling.provider.SampleData;
import com.feifan.sampling.util.LogUtil;

/**
 * Created by mengmeng on 16/6/29.
 */
public class ZoneHelper {

    public static void saveRemoteId(Context context,String id, String name){
        if(!TextUtils.isEmpty(id)){
            ZoneModel zone = new ZoneModel(name,id);
            // 保存到数据库
            ContentValues values = new ContentValues();
            zone.fill(values);
            Uri spotUri = context.getContentResolver().insert(SampleData.Zone.CONTENT_URI, values);
            zone.id = Integer.valueOf(spotUri.getLastPathSegment());
            LogUtil.i(Constants.DEBUG_TAG, zone.toString() + "'id is " + zone.id);
        }
    }
}
