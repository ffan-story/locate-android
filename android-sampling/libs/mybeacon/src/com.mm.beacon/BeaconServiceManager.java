/**
 * Radius Networks, Inc.
 * http://www.radiusnetworks.com
 * 
 * @author David G. Young
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.mm.beacon;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.mm.beacon.blue.ScanData;
import com.mm.beacon.data.Region;
import com.mm.beacon.service.BeaconRemoteService;

import java.util.ArrayList;
import java.util.List;

public class BeaconServiceManager implements BeaconDispatcher {

	private static final String TAG = "BeaconServiceManager";
	private Context mContext;
	private static BeaconServiceManager client = null;
	private BeaconFilter mBeaconfilter;
	private List<OnBeaconDetectListener> mBeaconDetectList = new ArrayList<OnBeaconDetectListener>();
	private IRemoteInterface mRemoteInterface;
	private int mDelay = 1000;
	private boolean mIsBinded = false;

	/**
	 * An accessor for the singleton instance of this class. A context must be provided, but if you need to use it from
	 * a non-Activity or non-Service class, you can attach it to another singleton or a subclass of the Android
	 * Application class.
	 */
	public static BeaconServiceManager getInstance(Context context) {
		if (!isInstantiated()) {
			client = new BeaconServiceManager(context);
		}
		return client;
	}


	/**
	 * Determines if the singleton has been constructed already. Useful for not overriding settings set declaratively in
	 * XML
	 *
	 * @return true, if the class has been constructed
	 */
	public static boolean isInstantiated() {
		return (client != null);
	}

	private BeaconServiceManager(Context context) {
		mContext = context;
		mBeaconfilter = new BeaconFilter();
	}

	public void setBeaconFilter(BeaconFilter beaconFilter){
		if(beaconFilter != null){
			mBeaconfilter = beaconFilter;
		}
	}

	public void setDelay(int delay){
		if(delay > 0) {
			mDelay = delay;
		}
	}

	public void startService(){
		bindService();
	}

	private ServiceConnection iBeaconServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mRemoteInterface = IRemoteInterface.Stub.asInterface(service);
			try {
				mRemoteInterface.registerCallback(mCallback);
				if(mBeaconfilter != null){
//					mRemoteInterface.setBeaconFilter();
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			if(mRemoteInterface != null){
				try {
					mRemoteInterface.unregisterCallback(mCallback);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	};

	public void bindService( ){
		if(mContext != null) {
			Intent intent = new Intent(mContext.getApplicationContext(), BeaconRemoteService.class);
			intent.putExtra(BeaconConstants.SCAN_INTERVAL,mDelay);
			mContext.bindService(intent, iBeaconServiceConnection, Context.BIND_AUTO_CREATE);
			mIsBinded = true;
		}
	}


	public void stopService() {
		unBindService();
	}

	public void unBindService(){
		if(mContext != null && mIsBinded) {
			mContext.getApplicationContext().unbindService(iBeaconServiceConnection);
			mIsBinded = false;
		}
	}

	public void registerBeaconListerner(OnBeaconDetectListener listener) {
		if (mBeaconDetectList != null && !mBeaconDetectList.contains(listener)) {
			mBeaconDetectList.add(listener);
		}
	}

	public void unRegisterBeaconListener(OnBeaconDetectListener listener) {
		if (mBeaconDetectList != null && mBeaconDetectList.contains(listener)) {
			mBeaconDetectList.remove(listener);
		}
	}

	private void clearListener() {
		if (mBeaconDetectList != null && !mBeaconDetectList.isEmpty()) {
			mBeaconDetectList.clear();
		}
	}

	/**
	 *
	 */
	private IBeaconDetect.Stub mCallback = new IBeaconDetect.Stub() {

		@Override
		public void onBeaconDetect(List<IBeacon> beaconlist) throws RemoteException {
				Log.e("callback",beaconlist.size()+"");
			if (mBeaconDetectList != null && beaconlist != null
					&& !beaconlist.isEmpty()) {
				for (int i = 0; i < mBeaconDetectList.size(); i++) {
					OnBeaconDetectListener beacon = mBeaconDetectList.get(i);
					if (beacon != null) {
						List<IBeacon> list = new ArrayList<IBeacon>();
						list.addAll(beaconlist);
						beacon.onBeaconDetected(list);
					}
				}
			}
		}

		@Override
		public void onRawDataDetect(List<IScanData> list){
			Log.e("onRawDataDetect",list.size()+"");
		}
	};

	public void onDestory(){
		unBindService();
	}

	@Override
	public void onBeaconDetect(List<IBeacon> beaconlist) {

	}

	@Override
	public void onBeaconRawDataDetect(List<ScanData> beaconlist) {

	}

	@Override
	public void onBeaconEnter(Region region) {

	}

	@Override
	public void onBeaconExit(Region region) {

	}

	public interface OnBeaconDetectListener {
		public void onBeaconDetected(List<IBeacon> beaconlist);
		public void onBeaconRawDataDetect(List<ScanData> beaconlist);
		public void onBeaconEnter(Region region);

		public void onBeaconExit(Region region);
	}

}
