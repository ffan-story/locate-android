package com.feifan.scan.base;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;

/**
 * 这个类主要是用于配置生成二维码一维码的参数
 * Created by Administrator on 2016/1/27.
 */
public class QRParams {
    //生成的宽度
    private int mWidth;
    //高度
    private int mHeight;
    //内容
    private String mContent;
    //在生成的二维码上面添加的图片，下面的mPicScale是logo相对于生成二维码的大小，例如是5 则大小是二维码的1/5
    private Bitmap mLogo;
    //二维码的前景色，默认是黑色
    private int mForground;
    //默认是白色 如果前景色和背景色设置不当会导致扫描时不好识别
    private int mBackGround;
    //二维码的logo的大小
    private int mPicScale = 5;
    //编码解码的格式
    private BarcodeFormat mCodeFormat;

    public BarcodeFormat getCodeFormat() {
        return mCodeFormat;
    }

    public void setCodeFormat(BarcodeFormat codeFormat) {
        this.mCodeFormat = codeFormat;
    }

    public int getPicScale() {
        return mPicScale;
    }

    public void setPicScale(int mPicScale) {
        this.mPicScale = mPicScale;
    }
    public int getBackGround() {
        return mBackGround;
    }

    public void setBackGround(int mBackGround) {
        this.mBackGround = mBackGround;
    }

    public int getForground() {
        return mForground;
    }

    public void setForground(int mForground) {
        this.mForground = mForground;
    }

    public Bitmap getLogo() {
        return mLogo;
    }

    public void setLogo(Bitmap mLogo) {
        this.mLogo = mLogo;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public QRParams buildWidth(int width){
        if(width > 0){
            setWidth(width);
        }
        return this;
    }

    public QRParams buildHeight(int height){
        if(height > 0){
            setHeight(height);
        }
        return this;
    }

    public QRParams buildLogo(Bitmap bitmap){
        if(bitmap != null){
            setLogo(bitmap);
        }
        return this;
    }

    public QRParams buildContent(String content){
        if(!TextUtils.isEmpty(content)){
            setContent(content);
        }
        return this;
    }

    public QRParams buildForground(int forground){
        if(forground > 0){
            setForground(forground);
        }
        return this;
    }

    public QRParams buildBackground(int background){
        if(background > 0){
            setBackGround(background);
        }
        return this;
    }

    public QRParams buildPicScale(int scale){
        if(scale > 0){
            setPicScale(scale);
        }
        return this;
    }

    public QRParams buildCodeFormat(BarcodeFormat format){
        if(format !=  null){
            setCodeFormat(format);
        }
        return this;
    }
}
