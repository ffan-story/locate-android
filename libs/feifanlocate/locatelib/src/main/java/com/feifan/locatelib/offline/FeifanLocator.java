package com.feifan.locatelib.offline;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.debuglib.window.DebugWindow;
import com.feifan.indoorlocation.model.IndoorLocationModel;
import com.feifan.locatelib.LocatorBase;
import com.feifan.locatelib.cache.BeaconStore;
import com.feifan.locatelib.cache.CacheState;
import com.feifan.locatelib.cache.FingerprintStore;
import com.feifan.locatelib.cache.FingerprintStore.FPLocation;
import com.feifan.scanlib.beacon.SampleBeacon;

import java.util.Collection;
import java.util.List;

/**
 * Created by xuchunlei on 2016/11/7.
 */

public class FeifanLocator extends LocatorBase {

    private static final FeifanLocator INSTANCE = new FeifanLocator();

    // locate
    private FPLocation mLoc;
    private int[] mRecentFloor = new int[3]; // 保存最近三次的定位楼层
    private int mGuard = 0; // 循环计数
//    private SampleBeacon[] mTargetArray = new SampleBeacon[0];

    private FeifanLocator() {

    }

    public static final FeifanLocator getInstance() {
        return INSTANCE;
    }

    @Override
    protected void handleScanData(Collection<SampleBeacon> rawData, Collection<SampleBeacon> data) {

        if(!isStarted()) {
            return;
        }

        // 定位楼层
//        int floor = BeaconStore.getInstance().selectFloor(data.toArray(mTargetArray));
        int floor = BeaconStore.getInstance().selectFloor(rawData);
        // floor = 0 表示接受数据不足以定位
        mRecentFloor[mGuard++ % 3] = floor;
        updateFloor(floor);
        DebugWindow.get().logI(mRecentFloor[0] + "," + mRecentFloor[1] + "," + mRecentFloor[2]);

        // 定位位置
        List<SampleBeacon> dataList = BeaconStore.getInstance().process2List(data);
//        long begin = SystemClock.elapsedRealtimeNanos();
        mLoc = mFinder.selectLocation(dataList);
//        LogUtils.e("计算时间=" + (SystemClock.elapsedRealtimeNanos() - begin) / 1000000);
    }

    @Override
    protected void updateLocation(IndoorLocationModel model) {
        if(mLoc != null) {
            model.x = mLoc.x;
            model.y = mLoc.y;
            model.floor = mLoc.floor;
            model.timestamp = System.currentTimeMillis();
        }
    }

    private void updateFloor(int floor) {
        // 最近三次楼层均变化
        if(CacheState.getInstance().getFloor() != floor) {
            if(mRecentFloor[0] == floor && mRecentFloor[1] == floor && mRecentFloor[2] == floor) {
                if(mLoc == null) {
                    return;
                }
                mLoc.floor = floor;
                // 更新使用的指纹库
                FPLocation[] fps = FingerprintStore.getInstance().selectFingerprints(floor);
                mFinder.updateFingerprints(fps);
                CacheState.getInstance().setFloor(floor);
                LogUtils.w("floor changed to " + floor);
            }
        }
    }
}
