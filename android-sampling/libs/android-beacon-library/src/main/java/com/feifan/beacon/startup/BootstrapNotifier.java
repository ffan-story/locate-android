package com.feifan.beacon.startup;

import android.content.Context;

import com.feifan.beacon.MonitorNotifier;

public interface BootstrapNotifier extends MonitorNotifier {
    public Context getApplicationContext();
}
