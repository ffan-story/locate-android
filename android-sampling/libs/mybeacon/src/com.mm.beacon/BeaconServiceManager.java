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
import android.util.Log;

import com.mm.beacon.blue.ScanData;
import com.mm.beacon.data.IBeacon;
import com.mm.beacon.data.Region;
import com.mm.beacon.service.BeaconService;

import java.util.List;

public class BeaconServiceManager implements BeaconDispatcher {

	private static final String TAG = "BeaconServiceManager";
	private Context mContext;
	private static BeaconServiceManager client = null;
	private BeaconService mService;
	private BeaconFilter mBeaconfilter;



	/**
	 * An accessor for the singleton instance of this class. A context must be provided, but if you need to use it from
	 * a non-Activity or non-Service class, you can attach it to another singleton or a subclass of the Android
	 * Application class.
	 */
	public static BeaconServiceManager getInstance(Context context) {
		if (!isInstantiated()) {
			Log.d(TAG, "IBeaconManager instance craetion");
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

	public void startService(){
		bindService();
	}

	private ServiceConnection iBeaconServiceConnection = new ServiceConnection() {
		// Called when the connection with the service is established
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.d(TAG, "we have a connection to the service now");
			mService = ((BeaconService.BeaconBinder)service).getService();
			mService.setBeaconDisptcher(BeaconServiceManager.this);
		}

		// Called when the connection with the service disconnects unexpectedly
		public void onServiceDisconnected(ComponentName className) {
			Log.e(TAG, "onServiceDisconnected");
			mService.setBeaconDisptcher(null);
		}
	};

	public void bindService( ){
		if(mContext != null) {
			Intent intent = new Intent(mContext.getApplicationContext(), BeaconService.class);
			mContext.bindService(intent, iBeaconServiceConnection, Context.BIND_AUTO_CREATE);
		}
	}

	public void unBindService(){
		if(mContext != null) {
			mContext.unbindService(iBeaconServiceConnection);
		}
	}

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
}
