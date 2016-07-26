// IRemoteInterface.aidl
package com.mm.beacon;
import com.mm.beacon.FilterBeacon;
import com.mm.beacon.IBeaconDetect;
// Declare any non-default types here with import statements

interface IRemoteInterface {
    void registerCallback(IBeaconDetect cb);
    void unregisterCallback(IBeaconDetect cb);
    void setBeaconFilter(out List<FilterBeacon> list);
}