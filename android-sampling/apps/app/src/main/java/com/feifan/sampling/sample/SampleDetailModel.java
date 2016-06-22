package com.feifan.sampling.sample;

import android.database.Cursor;

import com.feifan.sampling.provider.SampleData.BeaconDetail;
import com.feifan.sampling.widget.SimpleAdapter.CursorModel;

/**
 * 样本详情数据模型
 *
 * Created by xuchunlei on 16/4/22.
 */
public class SampleDetailModel extends CursorModel {

    public String mUUID;
    public int mMajor;
    public int mMinor;
    public int mRSSI;

    public SampleDetailModel(Cursor cursor) {
        super(cursor);
        int uuidIndex = cursor.getColumnIndexOrThrow(BeaconDetail.UUID);
        mUUID = cursor.getString(uuidIndex);
        int majorIndex = cursor.getColumnIndexOrThrow(BeaconDetail.MAJOR);
        mMajor = cursor.getInt(majorIndex);
        int minorIndex = cursor.getColumnIndexOrThrow(BeaconDetail.MINOR);
        mMinor = cursor.getInt(minorIndex);
        int rssiIndex = cursor.getColumnIndexOrThrow(BeaconDetail.RSSI);
        mRSSI = cursor.getInt(rssiIndex);
    }

    @Override
    public String toString() {
        return "UUID:" + mUUID + "\n"
                + "Major:" + mMajor + "\n"
                + "Minor:" + mMinor + "\n"
                + "RSSI:" + mRSSI + "\n";
    }
}
