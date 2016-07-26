package com.feifan.scan.base;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.text.TextUtils;

import com.feifan.scan.common.QRHelper;
import com.feifan.scan.common.toolbox.IScanStack;
import com.feifan.scan.common.toolbox.ZXingStack;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

/**
 * Created by mengmeng on 16/1/26.
 */
public class ScanManager {
    private static ScanManager mScanManager;
    private IScanStack mScanStack;

    public static ScanManager getInstance(){
        if(mScanManager == null){
            synchronized (ScanManager.class){
                mScanManager = new ScanManager();
            }
        }
        return mScanManager;
    }

    private ScanManager(){
        mScanStack = new ZXingStack();
        mScanStack.init();
    }

    public void setScanStack(IScanStack stack){
        if(stack != null){
            mScanStack = stack;
        }
    }

    public String scanBitMap(Bitmap bitmap){
        return mScanStack.performBitmap(bitmap);
    }

    public Bitmap createQRImage(QRParams params){
        if(params != null){
            return createQRImage(params.getContent(),params.getWidth(),params.getHeight(),params.getLogo(),params.getForground(),params.getBackGround(),params.getPicScale(),params.getCodeFormat());
        }
        return null;
    }
    /**
     * 根据content生成二维码
     * @param content
     * @param QR_WIDTH
     * @param QR_HEIGHT
     * @return
     */
    public Bitmap createQRImage(String content, int QR_WIDTH, int QR_HEIGHT,Bitmap logoBm,int forgroundcolor,int backgroundcolor,int logoscale,BarcodeFormat format) {
        try {
            if(QR_HEIGHT == 0 || QR_WIDTH == 0){
               throw new IllegalArgumentException("the width or height can not be zero");
            }
            // 判断URL合法性
            if (TextUtils.isEmpty(content)) {
                throw new IllegalArgumentException("the content can not be null");
            }
            int background = QRHelper.DEFAULT_BACKGROUND;
            int forground = QRHelper.DEFAULT_FORGROUND;
            if(forgroundcolor > 0){
                forground = forgroundcolor;
            }
            if(backgroundcolor >0){
                background = backgroundcolor;
            }
            BarcodeFormat codeFormat = BarcodeFormat.QR_CODE;
            if(format != null){
                codeFormat = format;
            }

            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, QRHelper.DEFAULT_ENCODE);
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix;
            if(format == BarcodeFormat.QR_CODE){
                bitMatrix = new QRCodeWriter().encode(content,
                        codeFormat, QR_WIDTH, QR_HEIGHT, hints);
            }else {
                bitMatrix = new MultiFormatWriter().encode(content,
                        BarcodeFormat.CODE_128, QR_WIDTH, QR_HEIGHT, hints);
            }

            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = background;
                    } else {
                        pixels[y * QR_WIDTH + x] = forground;
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            // 显示到一个ImageView上面
            if(logoBm != null) {
                bitmap = addLogo(bitmap, logoBm,logoscale);
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 在二维码中间添加Logo图案
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo,int logoScale) {
        if (src == null) {
            return null;
        }

        if (logo == null) {
            return src;
        }

        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        //logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / logoScale / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }
}
