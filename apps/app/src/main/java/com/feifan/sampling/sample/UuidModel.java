package com.feifan.sampling.sample;

import android.database.Cursor;

import com.feifan.sampling.Constants;
import com.feifan.sampling.provider.SampleData.BeaconUUID;
import com.feifan.sampling.util.LogUtil;
import com.feifan.sampling.widget.SimpleAdapter;

/**
 * Created by xuchunlei on 16/5/6.
 */
public class UuidModel extends SimpleAdapter.CursorModel {
    public String uuid;

    public UuidModel(Cursor cursor) {
        super(cursor);
        int idIndex = cursor.getColumnIndexOrThrow(BeaconUUID._ID);
        id = cursor.getInt(idIndex);
        int nameIndex = cursor.getColumnIndexOrThrow(BeaconUUID.UUID);
        uuid = cursor.getString(nameIndex);
        LogUtil.d(Constants.DEBUG_TAG, "a new zone was created");
    }

    @Override
    public String toString() {
        return uuid;
    }
}
