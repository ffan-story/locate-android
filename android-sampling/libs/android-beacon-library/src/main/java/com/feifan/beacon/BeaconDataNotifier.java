package com.feifan.beacon;

import com.feifan.beacon.client.DataProviderException;

/**
 * Notifies when server-side com.my.com.my.com.mm.beacon data are available from a web service.
 */
public interface BeaconDataNotifier {
    /**
     * This method is called after a request to get or sync com.my.com.my.com.mm.beacon data
     * If fetching data was successful, the data is returned and the exception is null.
     * If fetching of the data is not successful, an exception is provided.
     * @param beacon
     * @param data
     * @param exception
     */
    public void beaconDataUpdate(Beacon beacon, BeaconData data, DataProviderException exception);
}
