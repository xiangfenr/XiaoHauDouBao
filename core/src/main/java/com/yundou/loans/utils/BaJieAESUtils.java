package com.yundou.loans.utils;

import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class BaJieAESUtils {

    private static final String TAG = "AesUtil";

    /** 加密模式 */
    public static final String AES_ECB = "AES/ECB/PKCS5Padding";
    public static final String AES_CBC = "AES/CBC/PKCS5Padding";
    public static final String AES_CFB = "AES/CFB/PKCS5Padding";

    /** AES IV 长度 */
    public static final int IV_LENGTH = 16;

    /***
     * 空校验
     */
    public static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }

    /***
     * String 转 byte
     */
    public static byte[] getBytes(String str) {
        if (isEmpty(str)) {
            return null;
        }
        return str.getBytes(StandardCharsets.UTF_8);
    }

    /***
     * 初始化向量（IV）
     * 生成 16 位随机字符串
     */
    public static String getIv() {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < IV_LENGTH; i++) {
            sb.append(str.charAt(random.nextInt(str.length())));
        }
        return sb.toString();
    }

    /***
     * 获取 AES Key
     */
    public static SecretKeySpec getSecretKeySpec(String key) {
        return new SecretKeySpec(
                Objects.requireNonNull(getBytes(key)),
                "AES"
        );
    }

    /**
     * 加密 - ECB
     */
    public static String encrypt(String text, String key) {
        if (isEmpty(text) || isEmpty(key)) return null;

        try {
            Cipher cipher = Cipher.getInstance(AES_ECB);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKeySpec(key));
            byte[] encrypted = cipher.doFinal(getBytes(text));
            return Base64.encodeToString(encrypted, Base64.NO_WRAP);
        } catch (Exception e) {
            Log.e(TAG, "ECB encrypt error", e);
            return null;
        }
    }

    /**
     * 解密 - ECB
     */
    public static String decrypt(String text, String key) {
        if (isEmpty(text) || isEmpty(key)) return null;

        try {
            byte[] decoded = Base64.decode(text, Base64.NO_WRAP);
            Cipher cipher = Cipher.getInstance(AES_ECB);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKeySpec(key));
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e(TAG, "ECB decrypt error", e);
            return null;
        }
    }

    /**
     * 加密 - 自定义模式（CBC / CFB）
     */
    public static String encrypt(String text, String key, String iv, String mode) {
        if (isEmpty(text) || isEmpty(key) || isEmpty(iv)) return null;

        try {
            Cipher cipher = Cipher.getInstance(mode);
            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    getSecretKeySpec(key),
                    new IvParameterSpec(getBytes(iv))
            );
            byte[] encrypted = cipher.doFinal(getBytes(text));
            return Base64.encodeToString(encrypted, Base64.NO_WRAP);
        } catch (Exception e) {
            Log.e(TAG, "encrypt error", e);
            return null;
        }
    }

    /**
     * 解密 - 自定义模式（CBC / CFB）
     */
    public static String decrypt(String text, String key, String iv, String mode) {
        if (isEmpty(text) || isEmpty(key) || isEmpty(iv)) return null;

        try {
            byte[] decoded = Base64.decode(text, Base64.NO_WRAP);
            Cipher cipher = Cipher.getInstance(mode);
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    getSecretKeySpec(key),
                    new IvParameterSpec(getBytes(iv))
            );
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e(TAG, "decrypt error", e);
            return null;
        }
    }

    /** CBC 封装 */
    public static String encryptCbc(String text, String key, String iv) {
        return encrypt(text, key, iv, AES_CBC);
    }

    public static String decryptCbc(String text, String key, String iv) {
        return decrypt(text, key, iv, AES_CBC);
    }

    /**
     * 兼容旧逻辑的 AES 加密（SHA1PRNG）
     */
    public static String AesEncode(String content, String password) {
        try {
            byte[] encryptResult = encryptByte(content, password);
            return Base64.encodeToString(encryptResult, Base64.NO_WRAP);
        } catch (Exception e) {
            Log.e(TAG, "AesEncode error", e);
            return null;
        }
    }

    public static String AesDecode(String content, String password) {
        try {
            byte[] decoded = Base64.decode(content, Base64.NO_WRAP);
            byte[] decryptResult = decryptByte(decoded, password);
            return new String(decryptResult, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e(TAG, "AesDecode error", e);
            return content;
        }
    }

    /** 内部 byte 加解密（旧方案） */
    private static byte[] encryptByte(String content, String password) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(password.getBytes());
        keyGenerator.init(128, secureRandom);

        SecretKey secretKey = keyGenerator.generateKey();
        SecretKeySpec key = new SecretKeySpec(secretKey.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] decryptByte(byte[] content, String password) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(password.getBytes());
        keyGenerator.init(128, secureRandom);

        SecretKey secretKey = keyGenerator.generateKey();
        SecretKeySpec key = new SecretKeySpec(secretKey.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(content);
    }

}
