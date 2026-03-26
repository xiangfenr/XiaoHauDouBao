package com.yundou.loans.utils;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class JYQBDESUtils {

    // 算法名称
    private static final String ALGORITHM = "DESede";
    // 转换模式
    private static final String TRANSFORMATION = "DESede/CBC/PKCS5Padding";

    /**
     * 加密方法
     *
     * @param plainText   明文
     * @param aesKey      Key
     * @param ivParameter IV
     * @return {@link String}
     */
    public static String encrypt(String plainText, String aesKey, String ivParameter) {
        try {
            // 创建 DESedeKeySpec 对象
            DESedeKeySpec keySpec = new DESedeKeySpec(aesKey.getBytes(StandardCharsets.UTF_8));
            // 创建 SecretKeyFactory 对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            // 生成 SecretKey 对象
            SecretKey secretKey = keyFactory.generateSecret(keySpec);

            // 创建 IvParameterSpec 对象
            IvParameterSpec ivSpec = new IvParameterSpec(ivParameter.getBytes(StandardCharsets.UTF_8));

            // 创建 Cipher 对象并初始化为加密模式
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            // 执行加密操作
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            //原java写法
//            return java.util.Base64.getEncoder().encodeToString(encryptedBytes);

            // 将加密后的字节数组进行 Base64 编码
            return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解密方法
     *
     * @param encryptedText 密文
     * @param aesKey        Key
     * @param ivParameter   IV
     * @return 解密后的明文
     */
    public static String decrypt(String encryptedText, String aesKey, String ivParameter) {
        try {
            //原java写法
//            byte[] encryptedBytes = java.util.Base64.getDecoder().decode(encryptedText);
            // 将 Base64 编码的字符串解码为字节数组
            byte[] encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT);

            // 创建 DESedeKeySpec 对象
            DESedeKeySpec keySpec = new DESedeKeySpec(aesKey.getBytes(StandardCharsets.UTF_8));
            // 创建 SecretKeyFactory 对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            // 生成 SecretKey 对象
            SecretKey secretKey = keyFactory.generateSecret(keySpec);

            // 创建 IvParameterSpec 对象
            IvParameterSpec ivSpec = new IvParameterSpec(ivParameter.getBytes(StandardCharsets.UTF_8));

            // 创建 Cipher 对象并初始化为解密模式
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            // 执行解密操作
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // 将解密后的字节数组转换为字符串
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    /***
     * 初始化向量（IV）
     * 生成 16 位随机字符串
     */
    public static String getReqNo() {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 16; i++) {
            sb.append(str.charAt(random.nextInt(str.length())));
        }
        return sb.toString();
    }



}
