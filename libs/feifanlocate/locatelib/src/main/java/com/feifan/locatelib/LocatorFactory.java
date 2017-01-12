package com.feifan.locatelib;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.indoorlocation.IIndoorLocator;
import com.feifan.locatelib.offline.FeifanLocator;
import com.feifan.locatelib.online.OnLineLocator;

/**
 * 定位器工厂
 * Created by xuchunlei on 2016/11/7.
 */

public final class LocatorFactory {

    public static final int LOCATING_MODE_ONLINE = 1;
    public static final int LOCATING_MODE_OFFLINE = 2;

    private LocatorFactory() {

    }

    public static IIndoorLocator getDefaultLocator() {
//        return FeifanLocator.getInstance();
        return OnLineLocator.getInstance();
    }

    public static IIndoorLocator getLocator(int mode) {
        switch (mode) {
            case LOCATING_MODE_ONLINE:
                return OnLineLocator.getInstance();
            case LOCATING_MODE_OFFLINE:
                return FeifanLocator.getInstance();
            default:
                LogUtils.w("we didn't find mode " + mode + " locator, so provide a default one");
                return getDefaultLocator();
        }
    }
}
