package io.wangk.peekaboo.common.social.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtil {

    private static final char[] DIGIT = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static String byteToStr(byte[] byteArray) {
        StringBuilder strDigest = new StringBuilder();
        for (byte b : byteArray) {
            strDigest.append(byteToHexStr(b));
        }
        return strDigest.toString();
    }

    private static String byteToHexStr(byte mByte) {
        char[] tempArr = new char[2];
        tempArr[0] = DIGIT[(mByte >>> 4) & 0X0F];
        tempArr[1] = DIGIT[mByte & 0X0F];
        return new String(tempArr);
    }

    public static String sha1(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(content.getBytes());
            return byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating SHA-1 hash: " + e.getMessage(), e);
        }
    }
}
