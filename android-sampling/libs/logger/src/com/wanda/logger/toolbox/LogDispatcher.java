package com.wanda.logger.toolbox;

import java.util.concurrent.BlockingQueue;

/**
 * Created by mengmeng on 15/6/9.
 */
public class LogDispatcher extends Thread{

    /** The queue of requests to service. */
    private final BlockingQueue<Request> mQueue;
    /** Used for telling us to die. */
    private volatile boolean mQuit = false;
    private boolean isRun = false;
    private int m = 0;
    private Stack mStack;

    public LogDispatcher(BlockingQueue<Request> mQueue) {
        this.mQueue = mQueue;
    }

    @Override
    public void run() {
        super.run();
        Request request;
        while(true){
            try {
                // Take a request from the queue.
                request = mQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                // We may have been interrupted because it was time to quit.
                if (mQuit) {
                    return;
                }
                continue;
            }
            if(request != null){
                Object obj = request.getLog();
                if(obj != null) {
                    request.writeLog(obj);
                }
            }
        }
    }

    /**
     * Forces this dispatcher to quit immediately.  If any requests are still in
     * the queue, they are not guaranteed to be processed.
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }
}
