package com.feifan.locate.sampling.model;

import android.database.Cursor;

import com.feifan.locate.widget.cursorwork.CursorModel;

/**
 * Created by xuchunlei on 16/10/14.
 */

public class WorkLineModel extends CursorModel {

    public int oneId;
    public float oneX;
    public float oneY;
    public int twoId;
    public float twoX;
    public float twoY;

    public WorkLineModel(Cursor cursor) {
        super(cursor);
        int oneIdIndex = cursor.getColumnIndexOrThrow("one_id");
        oneId = cursor.getInt(oneIdIndex);
        int oneXIndex = cursor.getColumnIndexOrThrow("one_x");
        oneX = cursor.getFloat(oneXIndex);
        int oneYIndex = cursor.getColumnIndexOrThrow("one_y");
        oneY = cursor.getFloat(oneYIndex);

        int twoIdIndex = cursor.getColumnIndexOrThrow("two_id");
        twoId = cursor.getInt(twoIdIndex);
        int twoXIndex = cursor.getColumnIndexOrThrow("two_x");
        twoX = cursor.getFloat(twoXIndex);
        int twoYIndex = cursor.getColumnIndexOrThrow("two_y");
        twoY = cursor.getFloat(twoYIndex);
    }

    @Override
    public String toString() {
        return "id=" + id + ",one(" + oneId + "," + oneX + "," + oneY + "),two("
                + twoId + "," + twoX + "," + twoY + ")";
    }
}
