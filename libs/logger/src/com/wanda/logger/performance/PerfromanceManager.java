package com.wanda.logger.performance;

import android.content.Context;

/**
 * Created by mengmeng on 15/9/28.
 * http://doc.okbase.net/147181/archive/99778.html
 * 后续的的详细记录的问题可以参考这个项目进行测试
 */
public class PerfromanceManager {

    private Context mContext;
    private static PerfromanceManager mPerfromance;
    private TrafficInfo mTranficInfo;

    private PerfromanceManager(Context context){
        mContext = context;
        if(mContext != null){
            int uid = mContext.getApplicationInfo().uid;
            mTranficInfo = new TrafficInfo(String.valueOf(uid));
        }
    }

    public static PerfromanceManager getInstance(Context context){
        if(mPerfromance == null){
            synchronized (PerfromanceManager.class) {
                if (mPerfromance == null) {
                    mPerfromance = new PerfromanceManager(context);
                }
            }
        }
        return mPerfromance;
    }

}
