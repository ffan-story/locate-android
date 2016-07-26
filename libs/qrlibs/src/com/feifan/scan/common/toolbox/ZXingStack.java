package com.feifan.scan.common.toolbox;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.feifan.scan.base.MyPlanarYUVLuminanceSource;
import com.feifan.scan.common.controller.IViewFinder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/25.
 */
public class ZXingStack extends IScanStack<BarcodeFormat> {
    private static final String TAG = "ZXingScanner";
    private MultiFormatReader mMultiFormatReader;
    public static final List<BarcodeFormat> ALL_FORMATS = new ArrayList<BarcodeFormat>();
    private List<BarcodeFormat> mFormats;

    static {
        ALL_FORMATS.add(BarcodeFormat.UPC_A);
        ALL_FORMATS.add(BarcodeFormat.UPC_E);
        ALL_FORMATS.add(BarcodeFormat.EAN_13);
        ALL_FORMATS.add(BarcodeFormat.EAN_8);
        ALL_FORMATS.add(BarcodeFormat.RSS_14);
        ALL_FORMATS.add(BarcodeFormat.CODE_39);
        ALL_FORMATS.add(BarcodeFormat.CODE_93);
        ALL_FORMATS.add(BarcodeFormat.CODE_128);
        ALL_FORMATS.add(BarcodeFormat.ITF);
        ALL_FORMATS.add(BarcodeFormat.CODABAR);
        ALL_FORMATS.add(BarcodeFormat.QR_CODE);
        ALL_FORMATS.add(BarcodeFormat.DATA_MATRIX);
        ALL_FORMATS.add(BarcodeFormat.PDF_417);
    }

    public ZXingStack() {
    }


    @Override
    public void init() {
        Map<DecodeHintType, Object> hints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, getFormats());
        mMultiFormatReader = new MultiFormatReader();
        mMultiFormatReader.setHints(hints);
    }

    public Collection<BarcodeFormat> getFormats() {
        if (mFormats == null) {
            return ALL_FORMATS;
        }
        return mFormats;
    }

    @Override
    public void setFormats(List<BarcodeFormat> formats) {
        mFormats = formats;
        init();
    }

    @Override
    public boolean performScan(byte[] data, int width, int height) {
        Result rawResult = null;
        MyPlanarYUVLuminanceSource source = buildLuminanceSource(data, width, height);
        if (source != null) {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                rawResult = mMultiFormatReader.decodeWithState(bitmap);
                IViewFinder viewFinder = getViewFinder();
                int[] pixels = getCroppedGreyscaleBitmap(data, width, height);
                if (viewFinder != null && pixels != null) {
                    refreshScannerView(viewFinder, pixels, rawResult, source);
                }
            } catch (Exception re) {
                // continue
            } catch (OutOfMemoryError e) {

            } finally {
                mMultiFormatReader.reset();
            }
        }

        if (rawResult != null) {
            if (mOnScanCallBack != null) {
                Log.e(TAG, rawResult.getText());
                mOnScanCallBack.onScanSucess(rawResult.getText(), rawResult.getBarcodeFormat().toString());
            }
            return true;
        }
        return false;
    }

    private void refreshScannerView(final IViewFinder viewFinder, final int[] pixels, final Result rawResult, final MyPlanarYUVLuminanceSource source) {
        if (viewFinder != null && pixels != null && rawResult != null && source != null) {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    viewFinder.performZXingSucess(rawResult, source.renderCroppedGreyscaleBitmap());
                }
            });
        }
    }

    @Override
    public void setPreViewRect(Rect rect) {
        mRect = rect;
    }

    public MyPlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        if (mRect == null) {
            throw new IllegalArgumentException("the preview rect can not be null");
        }
        // Go ahead and assume it's YUV rather than die.
        MyPlanarYUVLuminanceSource source = null;

        try {
            source = new MyPlanarYUVLuminanceSource(data, width, height, mRect.left, mRect.top,
                    mRect.width(), mRect.height(), false);
        } catch (Exception e) {

        } catch (OutOfMemoryError e) {

        }
        return source;
    }

    public int[] getCroppedGreyscaleBitmap(byte[] data, int width, int height) {
        try {
            if (mRect != null) {
                int[] pixels = new int[width * height];
                byte[] yuv = data;
                int inputOffset = mRect.top * width + mRect.left;

                for (int y = 0; y < height; ++y) {
                    int outputOffset = y * width;

                    for (int x = 0; x < width; ++x) {
                        int grey = yuv[inputOffset + x] & 255;
                        pixels[outputOffset + x] = -16777216 | grey * 65793;
                    }

                    inputOffset += width;
                }

                return pixels;

            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public boolean performBitmapScan(Bitmap bitmap) {
        try {
            if (bitmap != null) {
                Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
                hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int[] pixels = new int[width * height];
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
                QRCodeReader reader = new QRCodeReader();
                Result rawResult = null;
                try {
                    rawResult = reader.decode(bitmap1, hints);
                } catch (NotFoundException e) {
                    e.printStackTrace();
                } catch (ChecksumException e) {
                    e.printStackTrace();
                } catch (FormatException e) {
                    e.printStackTrace();
                }
                if (rawResult != null) {
                    if (mOnScanCallBack != null) {
                        mOnScanCallBack.onScanSucess(rawResult.getText(), rawResult.getBarcodeFormat().toString());
                    }
                    return true;
                }
            }
        } catch (OutOfMemoryError e) {

        } catch (Exception e) {

        }
        return false;
    }

    @Override
    public String performBitmap(Bitmap bitmap) {
        try {
            if (bitmap != null) {
                Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
                hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int[] pixels = new int[width * height];
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
                QRCodeReader reader = new QRCodeReader();
                Result rawResult = null;
                try {
                    rawResult = reader.decode(bitmap1, hints);
                } catch (NotFoundException e) {
                    e.printStackTrace();
                } catch (ChecksumException e) {
                    e.printStackTrace();
                } catch (FormatException e) {
                    e.printStackTrace();
                }
                if (rawResult != null) {
                    return rawResult.getText();
                }
            }
        } catch (OutOfMemoryError e) {

        } catch (Exception e) {

        }
        return "";
    }

    @Override
    public void onStop() {
        mMultiFormatReader = null;
        if (mFormats != null) {
            mFormats.clear();
            mFormats = null;
        }
    }
}
