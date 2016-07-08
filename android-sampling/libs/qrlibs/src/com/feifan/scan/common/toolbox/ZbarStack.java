package com.feifan.scan.common.toolbox;

/**
 * Created by Administrator on 2016/1/25.
 */

public class ZbarStack{

}
//public class ZbarStack extends IScanStack<ZBarBarcodeFormat> {
//    private final String TAG = "ZbarScan";
//    private ImageScanner mScanner;
//    private List<ZBarBarcodeFormat> mFormats;
//
//    static {
//        System.loadLibrary("iconv");
//    }
//
//    public ZbarStack() {
//    }
//
//    @Override
//    public void init() {
//        mScanner = new ImageScanner();
//        mScanner.setConfig(0, Config.X_DENSITY, 3);
//        mScanner.setConfig(0, Config.Y_DENSITY, 3);
//
//        mScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
//        for (ZBarBarcodeFormat format : getFormats()) {
//            mScanner.setConfig(format.getId(), Config.ENABLE, 1);
//        }
//    }
//
//    public Collection<ZBarBarcodeFormat> getFormats() {
//        if (mFormats == null) {
//            return ZBarBarcodeFormat.ALL_FORMATS;
//        }
//        return mFormats;
//    }
//
//    @Override
//    public void setFormats(List<ZBarBarcodeFormat> formats) {
//        mFormats = formats;
//        init();
//    }
//
//    @Override
//    public boolean performScan(byte[] data, int width, int height) {
//        Image barcode = new Image(width, height, "Y800");
//        barcode.setData(data);
//        if(mScanner == null){
//            init();
//        }
//        int result = mScanner.scanImage(barcode);
//        Log.e(TAG,"scan result is: "+result);
//        if (result != 0) {
//            SymbolSet syms = mScanner.getResults();
//            final ZBarResult rawResult = new ZBarResult();
//            for (Symbol sym : syms) {
//                String symData = sym.getData();
//                if (!TextUtils.isEmpty(symData)) {
//                    rawResult.setContents(symData);
//                    rawResult.setBarcodeFormat(ZBarBarcodeFormat.getFormatById(sym.getType()));
//                    break;
//                }
//            }
//            if (rawResult != null) {
//                if (mOnScanCallBack != null) {
//                    Log.e(TAG,rawResult.getContents());
//                    mOnScanCallBack.onScanSucess(rawResult.getContents(), rawResult.getBarcodeFormat().getName());
//                }
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public boolean performBitmapScan(Bitmap bitmap) {
//        if (bitmap != null) {
//            int w = bitmap.getWidth();
//            int h = bitmap.getHeight();
//            int[] pixels = new int[w * h];
//            bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
//            Image barcode = new Image(w, h, "RGB4");
//            barcode.setData(pixels);
//            int result = mScanner.scanImage(barcode.convert("Y800"));
//            if (result != 0) {
//                SymbolSet syms = mScanner.getResults();
//                final ZBarResult rawResult = new ZBarResult();
//                for (Symbol sym : syms) {
//                    String symData = sym.getData();
//                    if (!TextUtils.isEmpty(symData)) {
//                        rawResult.setContents(symData);
//                        rawResult.setBarcodeFormat(ZBarBarcodeFormat.getFormatById(sym.getType()));
//                        break;
//                    }
//                }
//
//                if (rawResult != null) {
//                    if (mOnScanCallBack != null) {
//                        mOnScanCallBack.onScanSucess(rawResult.getContents(), rawResult.getBarcodeFormat().getName());
//                    }
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public String performBitmap(Bitmap bitmap) {
//        if (bitmap != null) {
//            int w = bitmap.getWidth();
//            int h = bitmap.getHeight();
//            int[] pixels = new int[w * h];
//            bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
//            Image barcode = new Image(w, h, "RGB4");
//            barcode.setData(pixels);
//            int result = mScanner.scanImage(barcode.convert("Y800"));
//            if (result != 0) {
//                SymbolSet syms = mScanner.getResults();
//                final ZBarResult rawResult = new ZBarResult();
//                for (Symbol sym : syms) {
//                    String symData = sym.getData();
//                    if (!TextUtils.isEmpty(symData)) {
//                        rawResult.setContents(symData);
//                        rawResult.setBarcodeFormat(ZBarBarcodeFormat.getFormatById(sym.getType()));
//                        break;
//                    }
//                }
//
//                if (rawResult != null) {
//                    return rawResult.getContents();
//                }
//            }
//        }
//        return "";
//    }
//
//    @Override
//    public void onStop() {
//
//    }
//
//}
