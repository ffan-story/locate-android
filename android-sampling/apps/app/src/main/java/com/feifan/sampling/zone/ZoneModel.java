package com.feifan.sampling.zone;

import android.content.ContentValues;
import android.database.Cursor;

import com.feifan.sampling.provider.SampleData.Zone;
import com.feifan.sampling.util.LogUtil;
import com.feifan.sampling.widget.SimpleAdapter.CursorModel;

/**
 * Created by xuchunlei on 16/5/6.
 */
public class ZoneModel extends CursorModel {

    private static final String TAG = "tag";

    /**
     * 定位区域名称
     */
    private String name;

    private String remoteId;

    public ZoneModel(String name,String id) {
        this.name = name;
        remoteId = id;
    }

    public ZoneModel(Cursor cursor) {
        super(cursor);
        int idIndex = cursor.getColumnIndexOrThrow(Zone._ID);
        id = cursor.getInt(idIndex);
        int nameIndex = cursor.getColumnIndexOrThrow(Zone.NAME);
        name = cursor.getString(nameIndex);
        int remoteIndex = cursor.getColumnIndexOrThrow(Zone.REMOTE_ID);
        remoteId = cursor.getString(remoteIndex);
        LogUtil.d(TAG, "a new zone was created");
    }

    /**
     * 填充数据
     * @param values
     */
    public void fill(ContentValues values) {
        values.put(Zone.NAME, name);
        values.put(Zone.REMOTE_ID, remoteId);
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString() {
        return name;
    }


    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }
}
