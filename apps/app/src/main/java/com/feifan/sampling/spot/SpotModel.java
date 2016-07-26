package com.feifan.sampling.spot;

import android.content.ContentValues;
import android.database.Cursor;

import com.feifan.sampling.provider.SampleData.Spot;

/**
 * 采集点模型类
 * <pre>
 *     封装采集点属性信息
 * </pre>
 *
 * Created by xuchunlei on 16/4/19.
 */
public class SpotModel {

    public int id;
    /** x轴坐标 */
    public float x;
    /** y轴坐标 */
    public float y;
    /** 方向角度，与参考系逆时针夹角 */
    public float d;
    /** beacon个数 */
    public int beaconCount;
    /** 采样个数 */
    public int sampleCount;

    public int remoteid;

    public SpotModel(float x, float y, float d,int remoteid) {
        this.x = x;
        this.y = y;
        this.d = d;
        this.remoteid = remoteid;
    }

    /**
     * 通过游标创建采集点模型
     * @param cursor
     * @return
     */
    public static SpotModel from(Cursor cursor) {
        int idIndex = cursor.getColumnIndexOrThrow(Spot._ID);
        int id = cursor.getInt(idIndex);
        int xIndex = cursor.getColumnIndexOrThrow(Spot.X);
        float x = cursor.getFloat(xIndex);
        int yIndex = cursor.getColumnIndexOrThrow(Spot.Y);
        float y = cursor.getFloat(yIndex);
        int dIndex = cursor.getColumnIndexOrThrow(Spot.D);
        float d =cursor.getFloat(dIndex);
        int remoteIndex = cursor.getColumnIndexOrThrow(Spot.REMOTE_ID);
        int remoteid =cursor.getInt(remoteIndex);
        SpotModel spot = new SpotModel(x, y, d,remoteid);
        spot.id = id;
        return spot;
    }

    /**
     * 填充数据
     * @param values
     */
    public void fill(ContentValues values) {
        values.put(Spot.X, x);
        values.put(Spot.Y, y);
        values.put(Spot.D, d);
        values.put(Spot.NAME, toString());
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + d + ")";
    }
}
