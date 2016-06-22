package com.feifan.sampling.sample;

import android.database.Cursor;
import android.provider.Settings;

import com.feifan.sampling.provider.SampleData.Sample;

/**
 * Created by xuchunlei on 16/4/25.
 */
public class SampleModel {

    public int mId;
    /** 样本名称 */
    public String mName;
    /** 采样的时间戳 */
    public double mTimeStamp;

    public static SampleModel from(Cursor cursor) {

        int idIndex = cursor.getColumnIndexOrThrow(Sample._ID);
        int id = cursor.getInt(idIndex);
        int nameIndex = cursor.getColumnIndexOrThrow(Sample.NAME);
        String name = cursor.getString(nameIndex);
        int timeIndex = cursor.getColumnIndexOrThrow(Sample.TIME);
        double time = cursor.getDouble(timeIndex);

        SampleModel sample = new SampleModel();
        sample.mId = id;
        sample.mName = name;
        sample.mTimeStamp = time;
        return sample;
    }

    @Override
    public String toString() {
        return mName + "\t\t\t\ttime:" + mTimeStamp;
    }
}
