/* Copyright (C) 2014 The bajlauncher Project
 * author    : xuchunlei
 * create at : 2014年11月26日下午4:02:42
 */
package com.feifan.locate.provider;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

/**
 * 内容提供者的工具类
 * @author xuchunlei
 *
 */
public class ProviderHelper {

	private static final HandlerThread sWorkerThread = new HandlerThread("feifan-locate");

	static {
		sWorkerThread.start();
	}

	private static final Handler sWorker = new Handler(sWorkerThread.getLooper());

	private ProviderHelper() {

	}

	private static HandlerThread getWorkerThread() {
		return sWorkerThread;
	}

	private static Handler getWorker() {
		return sWorker;
	}

	/**
	 * Runs the specified runnable immediately if called from the worker thread, otherwise it is
	 * posted on the worker thread handler.
	 */
	public static void runOnWorkerThread(Runnable r) {
		if (getWorkerThread().getThreadId() == Process.myTid()) {
			r.run();
		} else {
			// If we are not on the worker thread, then post to the worker handler
			getWorker().post(r);
		}
	}
}
