package com.feifan.scan.common.toolbox;


import java.util.List;
import java.util.ArrayList;

public class ZBarBarcodeFormat {
    private int mId;
    private String mName;

//    public static final ZBarBarcodeFormat NONE = new ZBarBarcodeFormat(Symbol.NONE, "NONE");
//    public static final ZBarBarcodeFormat PARTIAL = new ZBarBarcodeFormat(Symbol.PARTIAL, "PARTIAL");
//    public static final ZBarBarcodeFormat EAN8 = new ZBarBarcodeFormat(Symbol.EAN8, "EAN8");
//    public static final ZBarBarcodeFormat UPCE = new ZBarBarcodeFormat(Symbol.UPCE, "UPCE");
//    public static final ZBarBarcodeFormat ISBN10 = new ZBarBarcodeFormat(Symbol.ISBN10, "ISBN10");
//    public static final ZBarBarcodeFormat UPCA = new ZBarBarcodeFormat(Symbol.UPCA, "UPCA");
//    public static final ZBarBarcodeFormat EAN13 = new ZBarBarcodeFormat(Symbol.EAN13, "EAN13");
//    public static final ZBarBarcodeFormat ISBN13 = new ZBarBarcodeFormat(Symbol.ISBN13, "ISBN13");
//    public static final ZBarBarcodeFormat I25 = new ZBarBarcodeFormat(Symbol.I25, "I25");
//    public static final ZBarBarcodeFormat DATABAR = new ZBarBarcodeFormat(Symbol.DATABAR, "DATABAR");
//    public static final ZBarBarcodeFormat DATABAR_EXP = new ZBarBarcodeFormat(Symbol.DATABAR_EXP, "DATABAR_EXP");
//    public static final ZBarBarcodeFormat CODABAR = new ZBarBarcodeFormat(Symbol.CODABAR, "CODABAR");
//    public static final ZBarBarcodeFormat CODE39 = new ZBarBarcodeFormat(Symbol.CODE39, "CODE39");
//    public static final ZBarBarcodeFormat PDF417 = new ZBarBarcodeFormat(Symbol.PDF417, "PDF417");
//    public static final ZBarBarcodeFormat QRCODE = new ZBarBarcodeFormat(Symbol.QRCODE, "QRCODE");
//    public static final ZBarBarcodeFormat CODE93 = new ZBarBarcodeFormat(Symbol.CODE93, "CODE93");
//    public static final ZBarBarcodeFormat CODE128 = new ZBarBarcodeFormat(Symbol.CODE128, "CODE128");

    public static final List<ZBarBarcodeFormat> ALL_FORMATS = new ArrayList<ZBarBarcodeFormat>();

    static {
//        ALL_FORMATS.add(ZBarBarcodeFormat.PARTIAL);
//        ALL_FORMATS.add(ZBarBarcodeFormat.EAN8);
//        ALL_FORMATS.add(ZBarBarcodeFormat.UPCE);
//        ALL_FORMATS.add(ZBarBarcodeFormat.ISBN10);
//        ALL_FORMATS.add(ZBarBarcodeFormat.UPCA);
//        ALL_FORMATS.add(ZBarBarcodeFormat.EAN13);
//        ALL_FORMATS.add(ZBarBarcodeFormat.ISBN13);
//        ALL_FORMATS.add(ZBarBarcodeFormat.I25);
//        ALL_FORMATS.add(ZBarBarcodeFormat.DATABAR);
//        ALL_FORMATS.add(ZBarBarcodeFormat.DATABAR_EXP);
//        ALL_FORMATS.add(ZBarBarcodeFormat.CODABAR);
//        ALL_FORMATS.add(ZBarBarcodeFormat.CODE39);
//        ALL_FORMATS.add(ZBarBarcodeFormat.PDF417);
//        ALL_FORMATS.add(ZBarBarcodeFormat.QRCODE);
//        ALL_FORMATS.add(ZBarBarcodeFormat.CODE93);
//        ALL_FORMATS.add(ZBarBarcodeFormat.CODE128);
    }

    public ZBarBarcodeFormat(int id, String name) {
        mId = id;
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

//    public static ZBarBarcodeFormat getFormatById(int id) {
//        for(ZBarBarcodeFormat format : ALL_FORMATS) {
//            if(format.getId() == id) {
//                return format;
//            }
//        }
//        return ZBarBarcodeFormat.NONE;
//    }
}