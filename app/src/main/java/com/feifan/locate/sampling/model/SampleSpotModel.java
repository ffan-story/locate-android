package com.feifan.locate.sampling.model;

import android.database.Cursor;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.provider.LocateData.SampleSpot;
import com.feifan.locate.widget.cursorwork.CursorModel;

/**
 * 样本点Model
 * Created by xuchunlei on 16/9/7.
 */
public class SampleSpotModel extends CursorModel {

    public float direction;
    public int count;
    public int status;
    public String statusText;
    public int times;
    public int total;

    public SampleSpotModel(Cursor cursor) {
        super(cursor);
        int idIndex = cursor.getColumnIndexOrThrow(SampleSpot._ID);
        id = cursor.getInt(idIndex);
        int directionIndex = cursor.getColumnIndexOrThrow(SampleSpot.D);
        direction = cursor.getFloat(directionIndex);
        int countIndex = cursor.getColumnIndexOrThrow(SampleSpot.COUNT);
        count = cursor.getInt(countIndex);
        int statusIndex = cursor.getColumnIndexOrThrow(SampleSpot.STATUS);
        status = cursor.getInt(statusIndex);
        statusText = getStatusText(status);
        int timesIndex = cursor.getColumnIndexOrThrow(SampleSpot.TIMES);
        times = cursor.getInt(timesIndex);
        int totalIndex = cursor.getColumnIndexOrThrow(SampleSpot.TOTAL);
        total = cursor.getInt(totalIndex);

        LogUtils.d(toString());
    }

    private String getStatusText(int status) {
        switch (status) {
            case SampleSpot.STATUS_READY:
                return "就绪";
            case SampleSpot.STATUS_RUNNING:
                return "运行";
            case SampleSpot.STATUS_FINISH:
                return "完成";
            default:
                return "无效";
        }
    }

    @Override
    public String toString() {
        return String.format("%-10f%10d", direction, count);
    }
}
