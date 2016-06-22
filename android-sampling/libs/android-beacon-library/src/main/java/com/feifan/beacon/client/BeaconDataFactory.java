package com.feifan.beacon.client;

import com.feifan.beacon.Beacon;
import com.feifan.beacon.BeaconDataNotifier;

/**
 * This can be configured for the public com.my.com.my.com.mm.beacon data store, or a private com.my.com.my.com.mm.beacon data store.
 * In the public data store, you can read any value but only write to the values to the beacons you created
 *
 * @author dyoung
 *
 */
public interface BeaconDataFactory {
    /**
     * Asynchronous call
     * When data is available, it is passed back to the beaconDataNotifier interface
     * @param beacon
     */
    public void requestBeaconData(Beacon beacon, BeaconDataNotifier notifier);
}

