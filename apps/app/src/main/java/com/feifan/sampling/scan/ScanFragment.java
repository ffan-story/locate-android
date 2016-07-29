package com.feifan.sampling.scan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.feifan.sampling.Constants;
import com.feifan.sampling.R;
import com.feifan.sampling.base.log.config.APPLogConfig;
import com.feifan.sampling.base.log.request.CursorRequest;
import com.feifan.sampling.provider.SampleData;
import com.feifan.sampling.scan.model.CursorSaveModel;
import com.libs.base.sensor.dici.DiciService;
import com.libs.ui.fragments.CommonMenuFragment;
import com.libs.utils.DateTimeUtils;
import com.libs.utils.PrefUtil;
import com.mm.beacon.BeaconServiceManager;
import com.mm.beacon.IBeacon;
import com.mm.beacon.IScanData;
import com.mm.beacon.data.Region;
import com.wanda.logger.log.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mengmeng on 16/6/15.
 */
public class ScanFragment extends CommonMenuFragment implements BeaconServiceManager.OnBeaconDetectListener,DiciService.OnSensorCallBack {
  private Button mScanBtn;
  private TextView mScanStatusText;
  private EditText mIntervalEdit;
  private EditText mCountEdit;
  private BeaconServiceManager mBeaconManager;
  private List<IBeacon> mTemplist = new ArrayList<IBeacon>();
  private List<IScanData> mRawlist = new ArrayList<IScanData>();
  private Map<String, IBeacon> mMacBeacon = new HashMap<String, IBeacon>();
  private int mIntervalCount = 100;
  private int mInterval;
  private int mIntervalNum;
  private final String CVS_SCAN_SAVE_NAME = "scan_save_cvs";
  private final String RAW_SCAN_SAVE_NAME = "scan_save_raw";
  private final String COM_SCAN_SAVE_NAME = "scan_save_combine";
  private String mSpotid = "";
  private String mDirection = "";
  private float mRealDirection = 0f;
  private boolean mIsMenu = true;
  private float mPoint_x;
  private float mPoint_y;
  private DiciService mDiciService;
  private Handler mHandler = new Handler() {
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      if (mInterval == mIntervalCount) {
        mScanStatusText.setText(String.format(getString(R.string.scan_write_file))+"\nbeacon size is: "+mTemplist.size()+"\nraw blue size is: "+mRawlist.size());
        String beaconFileName = ScanHelper.getFilePathStr(CVS_SCAN_SAVE_NAME,mPoint_x,mPoint_y,mIntervalNum,mIntervalCount,mDirection);
        String[] header = buildHeader();
        ScanHelper.SaveIBeacon(header, getCvsData(), beaconFileName);
        String rawName = ScanHelper.getFilePathStr(RAW_SCAN_SAVE_NAME,mPoint_x,mPoint_y,mIntervalNum,mIntervalCount,mDirection);

        String[] rawheader = buildRawHeader();
        ScanHelper.SaveIBeacon(rawheader, getRawCvsData(), rawName);
        sendCombineCvs();
        mBeaconManager.stopService();
      }else if(mInterval < mIntervalCount) {
        mScanStatusText.setText(String.format(getString(R.string.scan_interval_txt),
                (mIntervalCount - mInterval)));
      }
    }
  };

  @Override
  public View onCreateCustomView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.biz_scan_fragment_layout, container, false);
    initView(view);
    return view;
  }

  @Override
  protected boolean isShowMenu() {
    return (mPoint_x > 0|| mPoint_y > 0);
  }

  private void initView(View view) {
    mScanBtn = (Button) view.findViewById(R.id.scan_btn);
    mScanStatusText = (TextView) view.findViewById(R.id.scan_status);
    mIntervalEdit = (EditText) view.findViewById(R.id.scan_interval_edit);
    mCountEdit = (EditText) view.findViewById(R.id.scan_count_edit);
    mScanBtn.setOnClickListener(new View.OnClickListener() {
      /**
       * Called when a view has been clicked.
       *
       * @param v The view that was clicked.
       */
      @Override
      public void onClick(View v) {
        String interval = mIntervalEdit.getEditableText().toString();
        String scanCount = mCountEdit.getEditableText().toString();
        if (!TextUtils.isEmpty(interval) && TextUtils.isDigitsOnly(interval)) {
          int scanInterval = Integer.valueOf(interval);
          mBeaconManager.setDelay(scanInterval);
        }
        if (!TextUtils.isEmpty(scanCount) && TextUtils.isDigitsOnly(scanCount)) {
          mIntervalCount = Integer.valueOf(scanCount);
        }

        mBeaconManager.startService();
      }
    });
    Bundle bundle = getArguments();
    if (bundle != null) {
      mSpotid = bundle.getString(Constants.EXTRA_KEY_SPOT_ID);
      String name = bundle.getString(Constants.EXTRA_KEY_SPOT_NAME);
      mDirection = bundle.getString(Constants.EXTRA_KEY_SPOT_DIRECTION);
      mPoint_x = bundle.getFloat(Constants.EXTRA_KEY_SPOT_X);
      mPoint_y = bundle.getFloat(Constants.EXTRA_KEY_SPOT_Y);
      setTitle(name);
    }
    initData();
  }

  private void initData(){
    mIntervalNum = PrefUtil.getInt(getContext(), Constants.SHAREPREFERENCE.RECYCLE_TIME_INTERVAL,Constants.SHAREPREFERENCE.DEFAULT_SCAN_TIME);
    mIntervalEdit.setText(mIntervalNum+"");
    int scancount = PrefUtil.getInt(getContext(), Constants.SHAREPREFERENCE.SCAN_MAX_COUNT,Constants.SHAREPREFERENCE.DEFAULT_SCAN_NUM);
    mCountEdit.setText(scancount+"");
    mDiciService = DiciService.getInstance(getActivity().getApplicationContext());
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mBeaconManager = BeaconServiceManager.getInstance(getActivity().getApplicationContext());
    mBeaconManager.registerBeaconListerner(this);
    mDiciService.startMagicScan();
    Bundle bundle = getArguments();
    if (bundle != null) {
      mPoint_x = bundle.getFloat(Constants.EXTRA_KEY_SPOT_X);
      mPoint_y = bundle.getFloat(Constants.EXTRA_KEY_SPOT_Y);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    mDiciService.registerSensorCallBack(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    mDiciService.unRegisterSensorCallBack(this);
  }

  @Override
  public void onBeaconDetected(List<IBeacon> beaconlist) {
    if (beaconlist != null && !beaconlist.isEmpty()) {
      buildScanIndex(beaconlist);
      mTemplist.addAll(beaconlist);
    }
    mInterval++;
    mHandler.sendEmptyMessage(mInterval);
  }

  private void buildScanIndex(List<IBeacon> beaconlist) {
    if (beaconlist != null && beaconlist.size() > 0) {
      for (int i = 0; i < beaconlist.size(); i++) {
        IBeacon beacon = beaconlist.get(i);
        beacon.setIndex(mInterval);
        beacon.setmDirection(mRealDirection);
        String mac = beacon.getMac();
        if (!TextUtils.isEmpty(mac)) {
          if (!mMacBeacon.containsKey(mac)) {
            mMacBeacon.put(mac, beacon);
          }
        }
      }
    }
  }

  private ArrayList<IBeacon> buildCombineData() {
    if (!mRawlist.isEmpty() && !mMacBeacon.isEmpty()) {
      ArrayList<IBeacon> beaconList = new ArrayList<IBeacon>();
      for (int i = 0; i < mRawlist.size(); i++) {
        IScanData data = mRawlist.get(i);
        if (data != null) {
          String mac = data.device.getAddress();
          IBeacon beacon = IBeacon.fromScanData(data);
          IBeacon ibeacon = mMacBeacon.get(mac);
          if (beacon != null) {
              beacon.setIndex(data.index);
              beacon.setTime(data.time);
              beacon.setMac(mac);
              beacon.setmDirection(data.direction);
              beaconList.add(beacon);
          } else {
            if (ibeacon != null) {
              IBeacon tempBeacon = ibeacon.clone();
              tempBeacon.setIndex(data.index);
              tempBeacon.setTime(data.time);
              tempBeacon.setRssi(data.rssi);
              tempBeacon.setmDirection(data.direction);
              beaconList.add(tempBeacon);
            }
          }
        }
      }
      return beaconList;
    }
    return null;
  }

  private void buildRawScanIndex(List<IScanData> beaconlist) {
    if (beaconlist != null && beaconlist.size() > 0) {
      for (int i = 0; i < beaconlist.size(); i++) {
        IScanData scanData = beaconlist.get(i);
        scanData.index = mInterval;
        scanData.direction = mRealDirection;
        Log.e("direction",mRealDirection+" ");
      }
    }
  }

  @Override
  public void onBeaconRawDataDetect(List<IScanData> beaconlist) {
    if (beaconlist != null && !beaconlist.isEmpty()) {
      buildRawScanIndex(beaconlist);
      mRawlist.addAll(beaconlist);
    }
  }

  private String[] buildHeader() {
    String[] header = new String[11];
    header[0] = "index";
    header[1] = "mac";
    header[2] = "uuid";
    header[3] = "major";
    header[4] = "minor";
    header[5] = "rssi";
    header[6] = "time";
    header[7] = "realdirection";
    header[8] = "loc_x";
    header[9] = "loc_y";
    header[10] = "loc_direc";
    return header;
  }

  private String[] buildRawHeader() {
    String[] header = new String[4];
    header[0] = "index";
    header[1] = "mac";
    header[2] = "rssi";
    header[3] = "time";
    return header;
  }

  private List<String[]> getRawCvsData() {
    if (mRawlist != null && mRawlist.size() > 0) {
      List<String[]> list = new ArrayList<String[]>();
      for (int i = 0; i < mRawlist.size(); i++) {
        IScanData scandata = mRawlist.get(i);
        if (scandata != null) {
          String[] items = new String[4];
          items[0] = String.valueOf(scandata.index);
          items[1] = String.valueOf(scandata.device.getAddress());
          items[2] = String.valueOf(scandata.rssi);
          items[3] = String.valueOf(scandata.time);
          list.add(items);
        }
      }
      return list;
    }
    return null;
  }

  private List<String[]> getCvsData() {
    if (mTemplist != null && mTemplist.size() > 0) {
      List<String[]> list = new ArrayList<String[]>();
      for (int i = 0; i < mTemplist.size(); i++) {
        IBeacon beacon = mTemplist.get(i);
        if (beacon != null) {
          String[] items = new String[11];
          items[0] = String.valueOf(beacon.getIndex());
          items[1] = String.valueOf(beacon.getMac());
          items[2] = beacon.getProximityUuid();
          items[3] = String.valueOf(beacon.getMajor());
          items[4] = String.valueOf(beacon.getMinor());
          items[5] = String.valueOf(beacon.getRssi());
          items[6] = String.valueOf(beacon.getTime());
          items[7] = String.valueOf(beacon.getmDirection());
          items[8] = String.valueOf(mPoint_x);
          items[9] = String.valueOf(mPoint_y);
          items[10] = String.valueOf(mDirection);
          list.add(items);
        }
      }
      return list;
    }
    return null;
  }

  private void sendCombineCvs() {
    ArrayList<IBeacon> beaconList = buildCombineData();
    ArrayList<String[]> combineList = getCombineCvsData(beaconList);
    String textStr = mScanStatusText.getText().toString();
    textStr = textStr +"\n combine size is: "+combineList.size();
    mScanStatusText.setText(textStr);
    String combineName = ScanHelper.getFilePathStr(COM_SCAN_SAVE_NAME,mPoint_x,mPoint_y,mIntervalNum,mIntervalCount,mDirection);
    String[] header = buildHeader();
    ScanHelper.SaveIBeacon(header, combineList, combineName);
    saveBeaconDb(beaconList);
  }

  private void saveBeaconDb(ArrayList<IBeacon> beaconList) {
    if (beaconList != null && beaconList.size() > 0) {
      CursorSaveModel model = new CursorSaveModel();
      model.setDirection(mDirection);
      model.setSpotId(mSpotid);
      model.setName(DateTimeUtils.getCurrentTime("yyyy-MM-dd HH-mm-ss") + "_"
          + mInterval + "_" + mIntervalCount);
      model.setUri(SampleData.BeaconDetail.CONTENT_URI);
      model.setList(beaconList);
      CursorRequest request =
          new CursorRequest(new APPLogConfig(""), getActivity().getApplicationContext());
      request.setLog(model);
      Logger.writeRequest(request);
      //通过spotid来判断当前界面是从drawlayout传过来的还是从spotlist界面传过来的
      if (!TextUtils.isEmpty(mSpotid)) {
        startUploadService(beaconList);
      }
    }
  }

  private void startUploadService(ArrayList<IBeacon> beaconList) {
    if (beaconList != null && !beaconList.isEmpty()) {
      Intent intent = new Intent(getActivity(), UploadService.class);
      intent.putParcelableArrayListExtra("beacon", beaconList);
      intent.putExtra("spotid", mSpotid);
      getActivity().startService(intent);
    }
  }

  private ArrayList<String[]> getCombineCvsData(List<IBeacon> beaconlist) {
    if (beaconlist != null && beaconlist.size() > 0) {
      ArrayList<String[]> list = new ArrayList<String[]>();
      for (int i = 0; i < beaconlist.size(); i++) {
        IBeacon beacon = beaconlist.get(i);
        if (beacon != null) {
          String[] items = new String[11];
          items[0] = String.valueOf(beacon.getIndex());
          items[1] = String.valueOf(beacon.getMac());
          items[2] = beacon.getProximityUuid();
          items[3] = String.valueOf(beacon.getMajor());
          items[4] = String.valueOf(beacon.getMinor());
          items[5] = String.valueOf(beacon.getRssi());
          items[6] = String.valueOf(beacon.getTime());
          items[7] = String.valueOf(beacon.getmDirection());
          items[8] = String.valueOf(mPoint_x);
          items[9] = String.valueOf(mPoint_y);
          items[10] = String.valueOf(mDirection);
          list.add(items);
        }
      }
      return list;
    }
    return null;
  }

  @Override
  public void onBeaconEnter(Region region) {

  }

  @Override
  public void onBeaconExit(Region region) {

  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mBeaconManager.stopService();
    mBeaconManager.unRegisterBeaconListener(this);
    mDiciService.stopMagicScan();
  }

  @Override
  public void onSensorCallBack(float[] prefvalues) {
    if (prefvalues != null && prefvalues.length > 0){
      float azimuth = (float) Math.toDegrees(prefvalues[0]);
      if(TextUtils.isEmpty(mDirection)){
        mDirection = String.valueOf(azimuth);
      }
      mRealDirection = azimuth;
      Log.e("direction",mRealDirection+" --- "+ System.currentTimeMillis());
    }
  }
}
