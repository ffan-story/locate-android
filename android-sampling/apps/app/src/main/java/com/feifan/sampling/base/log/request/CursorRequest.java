package com.feifan.sampling.base.log.request;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.feifan.sampling.provider.SampleData;
import com.feifan.sampling.scan.model.CursorSaveModel;
import com.mm.beacon.IBeacon;
import com.wanda.logger.toolbox.IConfig;
import com.wanda.logger.toolbox.Request;

import java.util.List;

/**
 * Created by mengmeng on 16/6/17.
 */
public class CursorRequest extends Request<CursorSaveModel> {
  private Context mContext;

  public CursorRequest(IConfig config,Context context) {
    super(config);
    mContext = context;
  }

  @Override
  public void writeLog(CursorSaveModel model) {
    if(model != null){
        String spotid = model.getSpotId();
        Uri uri = model.getUri();
        String sampleName = model.getName();
        String direction = model.getDirection();
        saveIBeaconDb(model.getList(),spotid,uri,sampleName,direction);
    }
  }

  public void saveIBeaconDb(List<IBeacon> beaconlist, String spotid, Uri uri, String sampleName, String direction) {
    if (beaconlist != null && !beaconlist.isEmpty() && !TextUtils.isEmpty(spotid) && uri != null) {
      //TODO
      int sampleId = Integer.valueOf(spotid);
      ContentValues sampleValues = new ContentValues();
      sampleValues.put(SampleData.Sample.NAME, "sample#" + sampleName);
      sampleValues.put(SampleData.Sample.TIME, System.currentTimeMillis());
      sampleValues.put(SampleData.Sample.SPOT, sampleId);
      Uri sampleUri = mContext.getContentResolver().insert(SampleData.Sample.CONTENT_URI, sampleValues);
      for (int i = 0; i < beaconlist.size(); i++) {
        IBeacon beacon = beaconlist.get(i);
        if (beacon != null) {
          ContentValues values = new ContentValues();
//          values.put(SampleData.Sample.NAME, beacon.getIndex());
          values.put(SampleData.Sample.TIME, beacon.getTime());
//          values.put(SampleData.Sample.SPOT, spotid);
          values.put(SampleData.BeaconDetail.UUID, beacon.getProximityUuid());
          values.put(SampleData.BeaconDetail.MAJOR, beacon.getMajor());
          values.put(SampleData.BeaconDetail.MINOR, beacon.getMinor());
            values.put(SampleData.BeaconDetail.RSSI, beacon.getRssi());
          values.put(SampleData.BeaconDetail.MAC, beacon.getMac());
          values.put(SampleData.BeaconDetail.DIRECTION, direction);
//          values.put(SampleData.BeaconDetail.ACCURACY, beacon.getIndex());
          values.put(SampleData.BeaconDetail.SAMPLE, sampleId);
          Uri uriDetails =
              mContext.getContentResolver().insert(uri, values);
          Log.e("beacon info", "a new ibeacon's details is added to " + uriDetails);
        }
      }
    }
  }

  /**
   * Compares this object to the specified object to determine their relative
   * order.
   *
   * @param another the object to compare to this instance.
   * @return a negative integer if this instance is less than {@code another};
   * a positive integer if this instance is greater than
   * {@code another}; 0 if this instance has the same order as
   * {@code another}.
   * @throws ClassCastException if {@code another} cannot be converted into something
   *                            comparable to {@code this} instance.
   */
  @Override
  public int compareTo(Request<CursorSaveModel> another) {
    return 0;
  }
}
