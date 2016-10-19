package com.feifan.locate.widget.cursorwork;

import android.database.Cursor;
import android.provider.BaseColumns;

/**
 * 所有游标model的父类
 * Created by xuchunlei on 16/9/9.
 */
public class CursorModel {

    public int id;

    public CursorModel() {

    }

    public CursorModel(Cursor cursor) {
        int idIndex = cursor.getColumnIndexOrThrow(BaseColumns._ID);
        id = cursor.getInt(idIndex);
    }
}
