package com.feifan.scanlib.beacon;

import java.util.Arrays;

/**
 * Created by xuchunlei on 16/9/1.
 */
public class IBeaconUtils {

    private static byte[] bytes = new byte[16];
    private final static char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    private IBeaconUtils() {

    }



    public static String calculateUUID(byte[] raw) {
        StringBuilder builder = new StringBuilder();

        Arrays.fill(bytes, (byte)0);
        builder.delete(0, builder.length());
        System.arraycopy(raw, 9, bytes, 0, 16);
        String hexString = bytesToHex(bytes);
        builder.append(hexString.substring(0,8));
        builder.append("-");
        builder.append(hexString.substring(8,12));
        builder.append("-");
        builder.append(hexString.substring(12,16));
        builder.append("-");
        builder.append(hexString.substring(16,20));
        builder.append("-");
        builder.append(hexString.substring(20,32));
        return builder.toString();
    }

    public static int calculateMajor(byte[] raw) {
        if(raw == null) {
            return 0;
        }
        return (raw[25] & 0xff) * 0x100 + (raw[26] & 0xff);
    }

    public static int calculateMinor(byte[] raw) {
        if(raw == null) {
            return 0;
        }
        return (raw[27] & 0xff) * 0x100 + (raw[28] & 0xff);
    }

    public static int calculateTxPower(byte[] raw){
        if(raw == null) {
            return 0;
        }
        return  (int)raw[29]; // this one is signed
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
