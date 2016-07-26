package com.feifan.sampling.provider;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

/**
 * 样本数据库观察者
 *
 * Created by xuchunlei on 16/4/21.
 */
public class GeneralObserver extends ContentObserver {

    /** 消息类型－新建样本 */
    public static final int MSG_NEW_SAMPLE = 1;

    // 消息类型
    private int mWhat;

    // 用于更新界面的Handler
    private Handler mHandler;
    /**
     * Creates a content observer.
     *
     * @param what 消息类型
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public GeneralObserver(int what, Handler handler) {
        super(handler);
        mWhat = what;
        mHandler = handler;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        Message msg = mHandler.obtainMessage(mWhat);
        msg.obj = uri;
        msg.sendToTarget();
    }
}
