package com.feifan.locatelib;

import com.feifan.indoorlocation.IIndoorLocator;
import com.feifan.locatelib.offline.FeifanLocator;
import com.feifan.locatelib.online.OnLineLocator;

/**
 * 定位器工厂
 * Created by xuchunlei on 2016/11/7.
 */

public final class LocatorFactory {

    private LocatorFactory() {

    }

    public static IIndoorLocator getDefaultLocator() {
//        return FeifanLocator.getInstance();
        return OnLineLocator.getInstance();
    }
}
