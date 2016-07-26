// IBeaconDetect.aidl
package com.mm.beacon;
import com.mm.beacon.IBeacon;
import com.mm.beacon.IScanData;
// Declare any non-default types here with import statements

interface IBeaconDetect {
   void onBeaconDetect(in List<IBeacon> list);
   void onRawDataDetect(in List<IScanData> list);
}
