package com.feifan.locatelib.online;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.indoorlocation.model.IndoorLocationModel;
import com.feifan.locatelib.LocatorBase;
import com.feifan.locatelib.network.HttpResult;
import com.feifan.locatelib.network.HttpResultSubscriber;
import com.feifan.locatelib.network.TransformUtils;

import java.util.Map;

/**
 * Created by xuchunlei on 2016/11/7.
 */

public final class OnLineLocator extends LocatorBase {

    private static final OnLineLocator INSTANCE = new OnLineLocator();


    private transient LocateQueryData mQueryData = new LocateQueryData();
    private RxFingerLocateService mLocateService;

    public static final OnLineLocator getInstance() {
        return INSTANCE;
    }

    @Override
    protected void handleScanData(Map<String, Float> data) {
        mQueryData.upDateTensor(data);
//        queryData.upDateTensor(MockServer.TENSOR_DATA_860100010060300001);
    }

    @Override
    protected void updateLocation(final IndoorLocationModel model) {
        if(!mQueryData.tensor.isEmpty()) {
            mLocateService.getLocation(mQueryData)
                    .compose(TransformUtils.<HttpResult<LocateInfo>>defaultSchedulers())
                    .subscribe(new HttpResultSubscriber<LocateInfo>() {

                        @Override
                        protected void _onError(Throwable e) {
                            LogUtils.e(e.getMessage());
                            //test
//                            mPanel.updateLog(e.getMessage());
                            LogUtils.e(e.getMessage());
                        }

                        @Override
                        protected void _onSuccess(LocateInfo data) {
                            if (data != null) {
                                LogUtils.d("we are in (" + data.x + "," + data.y + "," + data.floor + ") at "
                                        + System.currentTimeMillis());
                                //test debug
//                                mPanel.updateLog(data.x + "," + data.y + "," + data.floor);

                                model.x = data.x;
                                model.y = data.y;
                                model.floor = data.floor;
                                model.timestamp = System.currentTimeMillis();
                            }
                        }
                    });
        }
    }
}
