package com.feifan.scan.common.toolbox;

public class ZBarResult {
    private String mContents;
    private ZBarBarcodeFormat mBarcodeFormat;

    public void setContents(String contents) {
        mContents = contents;
    }

    public void setBarcodeFormat(ZBarBarcodeFormat format) {
        mBarcodeFormat = format;
    }

    public ZBarBarcodeFormat getBarcodeFormat() {
        return mBarcodeFormat;
    }

    public String getContents() {
        return mContents;
    }
}