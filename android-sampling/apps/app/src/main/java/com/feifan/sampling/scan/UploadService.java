package com.feifan.sampling.scan;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.feifan.sampling.http.ApiCreator;
import com.feifan.sampling.provider.SampleData;
import com.feifan.sampling.spot.model.SpotUploadModel;
import com.feifan.sampling.spot.request.UploadSpotInterface;
import com.libs.base.http.BpCallback;
import com.libs.base.model.BaseJsonBean;
import com.libs.utils.SystemUtils;
import com.mm.beacon.data.IBeacon;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by mengmeng on 16/5/23.
 */
public class UploadService extends Service {


  @Override
  public void onStart(Intent intent, int startId) {
    super.onStart(intent, startId);
    if (intent != null){
      ArrayList<IBeacon> beaconlist = intent.getParcelableArrayListExtra("beacon");
      String spotid = intent.getStringExtra("spotid");
      if(beaconlist != null && !beaconlist.isEmpty()){
        startUploadData(beaconlist,spotid);
      }
    }
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return super.onStartCommand(intent, flags, startId);
  }

  /**
   * Return the communication channel to the service.  May return null if
   * clients can not bind to the service.  The returned
   * {@link IBinder} is usually for a complex interface
   * that has been <a href="{@docRoot}guide/components/aidl.html">described using
   * aidl</a>.
   * <p/>
   * <p><em>Note that unlike other application components, calls on to the
   * IBinder interface returned here may not happen on the main thread
   * of the process</em>.  More information about the main thread can be found in
   * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
   * Threads</a>.</p>
   *
   * @param intent The Intent that was used to bind to this service,
   *               as given to {@link Context#bindService
   *               Context.bindService}.  Note that any extras that were included with
   *               the Intent at that point will <em>not</em> be seen here.
   * @return Return an IBinder through which clients can call on to the
   * service.
   */
  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }


  private void startUploadData(List<IBeacon> beaconlist,String spotid ){
    if(beaconlist != null && !beaconlist.isEmpty()) {
      int i = 0;
      for (; i < beaconlist.size(); i++) {
        IBeacon model = beaconlist.get(i);
        if(model != null){
            final String uuid = model.getProximityUuid();
            final int major = model.getMajor();
            final int minor = model.getMinor();
            final int rssi = model.getRssi();
            final long time = model.getTime();
            final int spot_id = Integer.valueOf(spotid);

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
      stopSelf();
    }
  }

//  private void startNetRequest(final List<PatchSpotModel> subList){
//    String jsonstring = "";
//    if (subList != null) {
//      Gson gson = new Gson();
//      jsonstring = gson.toJson(subList);
//      Log.e("service", jsonstring);
//    }
//    if(TextUtils.isEmpty(jsonstring)){
//      return;
//    }
//
//    UploadInterface request = ApiCreator.getInstance().createApi(UploadInterface.class);
//    Call<BaseJsonBean<SpotPatchUploadModel>> call = request.uploadSpot(jsonstring);
//    call.enqueue(new BpCallback<BaseJsonBean<SpotPatchUploadModel>>() {
//      @Override
//      public void onResponse(BaseJsonBean<SpotPatchUploadModel> helpCenterModel) {
//        List<SpotUploadModel> idList = helpCenterModel.getData().getIds();
//        if(idList != null && idList.size() == subList.size()) {
//          saveRemoteId(idList, subList);
//        }
//      }
//
//      @Override
//      public void onFailure(String message) {
//
//      }
//    });
//  }

//  private void saveRemoteId(List<SpotUploadModel> idList,List<PatchSpotModel> subList){
//    for (int i=0 ;i<idList.size();i++){
//      SpotUploadModel model = idList.get(i);
//      PatchSpotModel patModel = subList.get(i);
//      if(model != null && patModel != null){
//        saveRemoteId(patModel.getUuid(),patModel.getMajor(),patModel.getMinor(),patModel.getRssi(),model.getId());
//      }
//    }
//  }

  private void saveRemoteId(String uuid,int major,int minor,int rssi,String remoteid){
    ContentValues newValues = new ContentValues();
    newValues.put(SampleData.BeaconDetail.REMOTE_ID, remoteid);
    String where = "uuid="+uuid+"& major="+major+"&minor="+minor+"&rssi="+rssi ;
    getContentResolver().update(SampleData.BeaconDetail.CONTENT_URI, newValues, where, null);
  }
}
