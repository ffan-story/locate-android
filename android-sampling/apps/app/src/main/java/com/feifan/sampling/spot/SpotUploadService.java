package com.feifan.sampling.spot;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.feifan.sampling.http.ApiCreator;
import com.feifan.sampling.provider.SampleData;
import com.feifan.sampling.spot.model.SpotUploadModel;
import com.feifan.sampling.spot.request.UploadSpotInterface;
import com.libs.base.http.BpCallback;
import com.libs.base.model.BaseJsonBean;
import com.libs.utils.SystemUtils;

import retrofit2.Call;

/**
 * Created by mengmeng on 16/5/23.
 */
public class SpotUploadService extends IntentService {

  public SpotUploadService() {
    super("SpotUploadService");
  }

  /**
   * Creates an IntentService. Invoked by your subclass's constructor.
   *
   * @param name Used to name the worker thread, important only for debugging.
   */
  public SpotUploadService(String name) {
    super(name);
  }

  @Override
  public void onStart(Intent intent, int startId) {
    super.onStart(intent, startId);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return super.onStartCommand(intent, flags, startId);
  }

  /**
   * This method is invoked on the worker thread with a request to process.
   * Only one Intent is processed at a time, but the processing happens on a
   * worker thread that runs independently from other application logic.
   * So, if this code takes a long time, it will hold up other requests to
   * the same IntentService, but it will not hold up anything else.
   * When all requests have been handled, the IntentService stops itself,
   * so you should not call {@link #stopSelf}.
   *
   * @param intent The value passed to {@link Context#startService(Intent)}.
   */
  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent != null) {
      Bundle bundle = intent.getExtras();
      if (bundle != null) {
        final String uuid = bundle.getString(SpotConstant.UUID);
        final int major = bundle.getInt(SpotConstant.MAJOR);
        final int minor = bundle.getInt(SpotConstant.MINOR);
        final int rssi = bundle.getInt(SpotConstant.RSSI);
        final long time = bundle.getLong(SpotConstant.TIME);
        final int spot_id = bundle.getInt(SpotConstant.SPOT_ID);

        UploadSpotInterface request = ApiCreator.getInstance().createApi(UploadSpotInterface.class);
        Call<BaseJsonBean<SpotUploadModel>> call =
            request.uploadSpot(uuid, major, minor, rssi, time, SystemUtils.getBrand(), spot_id);
        call.enqueue(new BpCallback<BaseJsonBean<SpotUploadModel>>() {
          @Override
          public void onResponse(BaseJsonBean<SpotUploadModel> model) {
            if(model == null || model.getData() == null || model.getData().getId() == null){
              return;
            }
            String id = model.getData().getId();
            System.out.println("uploadservice: " + id);
            saveRemoteId(uuid,major,minor,rssi,id);
          }

          @Override
          public void onFailure(String message) {

          }
        });
      }
    }
  }


  private void saveRemoteId(String uuid,int major,int minor,int rssi,String remoteid){
    ContentValues newValues = new ContentValues();
    newValues.put(SampleData.BeaconDetail.REMOTE_ID, remoteid);
    String where = "uuid="+uuid+"& major="+major+"&minor="+minor+"&rssi="+rssi ;
    getContentResolver().update(SampleData.BeaconDetail.CONTENT_URI, newValues, where, null);
  }
}
