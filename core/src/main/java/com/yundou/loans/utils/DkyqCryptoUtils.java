package com.yundou.loans.utils;

import android.util.Base64;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Author: fenr
 * 时间: 2025/9/22
 * 类名: ACTIVITY
 * 简述: 贷款逾期处理
 */
public class DkyqCryptoUtils {

    public static String encryptPhone(String phone, String key) throws Exception {
        // 生成随机 IV
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16]; // AES block size is 16 bytes
        secureRandom.nextBytes(iv);

        // 创建密钥规格
        SecretKeySpec secretKeySpec = new
                SecretKeySpec(key.getBytes("UTF-8"), "AES");

        // 初始化加密器
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new

                IvParameterSpec(iv));

        // 加密数据
        byte[] encryptedBytes = cipher.doFinal(phone.getBytes("UTF-8"));

        // 合并 IV 和加密数据
        byte[] combined = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, iv.length,
                encryptedBytes.length);

        // 返回 Base64 编码的结果
        return Base64.encodeToString(combined, Base64.NO_WRAP);
    }

    public static String decryptPhone(String encrypted, String key)
            throws Exception {
        // 解码 Base64
        byte[] combined = Base64.decode(encrypted, Base64.NO_WRAP);

        // 分离 IV 和加密数据
        byte[] iv = new byte[16];
        byte[] encryptedBytes = new byte[combined.length - iv.length];
        System.arraycopy(combined, 0, iv, 0, iv.length);
        System.arraycopy(combined, iv.length, encryptedBytes, 0,
                encryptedBytes.length);

        // 创建密钥规格
        SecretKeySpec secretKeySpec = new
                SecretKeySpec(key.getBytes("UTF-8"), "AES");

        // 初始化解密器
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new
                IvParameterSpec(iv));

        // 解密数据
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, "UTF-8");

    }
}
