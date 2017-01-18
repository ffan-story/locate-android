package com.feifan.locate.testkit.sensor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.VideoView;

import java.util.Random;

/**
 * Created by xuchunlei on 2016/12/21.
 */

public class MapView extends SurfaceView implements SurfaceHolder.Callback{

    private boolean isDraw = false;
    private SurfaceHolder holder;
    private RenderThread renderThread;

    public MapView(Context context) {
        this(context, null);
    }

    public MapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        holder = this.getHolder();
        holder.addCallback(this);

        renderThread = new RenderThread();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        isDraw = true;
//        renderThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void startDraw() {
        isDraw = true;
        renderThread.start();
    }

    public void stopDraw() {
        isDraw = false;
    }

    private class RenderThread extends Thread {
        @Override
        public void run() {
            // 不停绘制界面
            while (isDraw) {
                drawUI();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            super.run();
        }
    }

    /**
     * 界面绘制
     */
    private void drawUI() {
        Canvas canvas = holder.lockCanvas();
        try {
            drawCanvas(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            holder.unlockCanvasAndPost(canvas);
        }
    }

    //
    private Random randomX = new Random(System.currentTimeMillis());
    private Random randomY = new Random(System.currentTimeMillis() + randomX.nextInt());
    private Random randomR = new Random(System.currentTimeMillis() + randomY.nextInt());
    private Random randomC = new Random(System.currentTimeMillis() + randomR.nextInt());
    private Paint mPaint = new Paint();
    private void drawCanvas(Canvas canvas) {
        // 在 canvas 上绘制需要的图形
        mPaint.setColor(Color.rgb(randomC.nextInt(255), randomC.nextInt(255), randomC.nextInt(255)));
        canvas.drawCircle(randomX.nextInt(600), randomY.nextInt(800), randomR.nextInt(50), mPaint);
    }

}
