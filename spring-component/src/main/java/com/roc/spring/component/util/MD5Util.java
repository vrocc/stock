package com.roc.spring.component.util;

import java.security.MessageDigest;


public class MD5Util {

    /**
     * 生成md5
     *
     * @param message
     * @return
     */
    public static String getMD5(String message) {
        String md5str = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] input = message.getBytes();
            byte[] buff = md.digest(input);
            md5str = bytesToHex(buff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str;
    }

    /**
     * 二进制转十六进制
     *
     * @param bytes
     * @return
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder md5str = new StringBuilder();
        int digital;
        for (byte aByte : bytes) {
            digital = aByte;

            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString().toUpperCase();
    }
}
