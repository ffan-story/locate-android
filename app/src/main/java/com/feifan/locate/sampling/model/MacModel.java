package com.feifan.locate.sampling.model;

import android.database.Cursor;

import com.feifan.locate.provider.LocateData.Mac;
import com.feifan.locate.widget.cursorwork.CursorModel;

/**
 * Created by xuchunlei on 16/9/29.
 */

public final class MacModel extends CursorModel {

    private String uuid;
    private int major;
    private int minor;
    private String address;

    public MacModel(Cursor cursor) {
        super(cursor);
        int uuidIndex = cursor.getColumnIndexOrThrow(Mac.UUID);
        uuid = cursor.getString(uuidIndex);
        int majorIndex = cursor.getColumnIndexOrThrow(Mac.MAJOR);
        major = cursor.getInt(majorIndex);
        int minorIndex = cursor.getColumnIndexOrThrow(Mac.MINOR);
        minor = cursor.getInt(minorIndex);
        int addressIndex = cursor.getColumnIndexOrThrow(Mac.ADDRESS);
        address = cursor.getString(addressIndex);
    }

    @Override
    public String toString() {
        return "uuid:" + uuid + "\t major:" + major + "\t minor:" + minor + "\n"
                + "mac:" + address;
    }
}
