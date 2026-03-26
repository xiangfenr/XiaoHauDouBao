package com.yundou.loans.utils;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class JYHAESUtils {
    private static final String AES = "AES";


    /**
     * AES加密
     */
    public static String encryptByAES(String contentStr, String key) {
        try {
            if(contentStr.isEmpty()) {
                return null;
            }
            byte[] raw = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(contentStr.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(encrypted, Base64.NO_WRAP);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
//
//    /**
//     * AES解密
//     */
//    public static String decryptByAES(String sSrc, String sKey) {
//        try {
//            if (sKey == null) {
//                System.out.print("Key为空null");
//                return null;
//            }
//            byte[] raw = sKey.getBytes(StandardCharsets.UTF_8);
//            SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
//            Cipher cipher = Cipher.getInstance(AES_EBC_PKCS5PADDING);
//            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
//            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);
//            try {
//                byte[] original = cipher.doFinal(encrypted1);
//                String originalString = new String(original, StandardCharsets.UTF_8);
//                return originalString;
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return null;
//        }
//    }
}