package com.feifan.locate.sampling.model;

import android.database.Cursor;

import com.feifan.locate.provider.Columns.SampleColumns;
import com.feifan.locate.widget.cursorwork.CursorModel;

/**
 * Created by xuchunlei on 16/10/13.
 */

public class SampleModel extends CursorModel {

    /** 样本名称 */
    public String name;
    /** 当前采集的样本总数 */
    public int total;
    /** 采集进度 */
    public String progress;
    /** 状态值 */
    public int status;

    public SampleModel(Cursor cursor) {
        super(cursor);
        int nameIndex = cursor.getColumnIndexOrThrow(SampleColumns._NAME);
        name = cursor.getString(nameIndex);
        int totalIndex = cursor.getColumnIndexOrThrow(SampleColumns._TOTAL);
        total = cursor.getInt(totalIndex);
        int statusIndex = cursor.getColumnIndexOrThrow(SampleColumns._STATUS);
        status = cursor.getInt(statusIndex);
        int progressIndex = cursor.getColumnIndexOrThrow(SampleColumns._PROGRESS);
        progress = cursor.getString(progressIndex);
    }

    @Override
    public String toString() {
        return name;
    }
}
