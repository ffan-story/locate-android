package com.feifan.locate.sampling.model;

import android.database.Cursor;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.provider.LocateData;
import com.feifan.locate.provider.LocateData.SampleLine;

/**
 * Created by xuchunlei on 16/10/13.
 */

public class SampleLineModel extends SampleModel {

    public float direction;
    private String showName;

    public SampleLineModel(Cursor cursor) {
        super(cursor);

        int directionIndex = cursor.getColumnIndexOrThrow(SampleLine.D);
        direction = cursor.getFloat(directionIndex);
        showName = name + "#" + String.format("%.2f", direction);
        LogUtils.d(toString());
    }

    @Override
    public String toString() {
        return showName;
    }
}
