package com.yundou.loans.utils;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;

import java.io.File;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

/**
 * <p>Utils初始化相关 </p>
 */
public class Utils {
    public static Charset UTF8 = Charset.forName("UTF-8");
    public static Application application;
    private static volatile Utils instance = null;

    private Utils() {

    }

    public static Utils getInstance() {
        if (instance == null) {
            synchronized (Utils.class) {
                if (instance == null) {
                    instance = new Utils();
                }
            }
        }
        return instance;
    }


    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(Application context) {
        Utils.application = context;
    }


    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (application != null) return application.getApplicationContext();
        throw new NullPointerException("u should init first");
    }

    public File getFilesDir(String type) {
        return getContext().getExternalFilesDir(type);
    }


    public static <T> T checkNotNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }


    public static boolean isIDCardValid(String idCard) {
        String regex = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])\\d{3}([0-9Xx])$";
        return idCard.matches(regex);
    }

    public static String hidePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 7) {
            return phoneNumber;
        }
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7);
    }

    /**
     * 智能姓名脱敏（自动检测中英文）
     * @param name 姓名
     * @return 脱敏后的姓名
     */
    public static String maskName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "*";
        }

        String trimmedName = name.trim();

        // 判断是否为中文姓名
        boolean isChinese = isChineseName(trimmedName);

        if (isChinese) {
            return maskChineseName(trimmedName);
        } else {
            return maskEnglishName(trimmedName);
        }
    }

    /**
     * 判断是否为中文姓名
     */
    private static boolean isChineseName(String name) {
        Pattern chinesePattern = Pattern.compile("[\\u4e00-\\u9fff]");
        return chinesePattern.matcher(name).find();
    }

    /**
     * 中文姓名脱敏
     */
    private static String maskChineseName(String name) {
        int length = name.length();

        switch (length) {
            case 1:
                return "*";
            case 2:
                return name.charAt(0) + "*";
            case 3:
                return name.charAt(0) + "*" + name.charAt(2);
            default: // 4个字及以上
                char firstChar = name.charAt(0);
                char lastChar = name.charAt(length - 1);
                int maskCount = length - 2;
                StringBuilder mask = new StringBuilder();
                for (int i = 0; i < maskCount; i++) {
                    mask.append("*");
                }
                return firstChar + mask.toString() + lastChar;
        }
    }

    /**
     * 英文姓名脱敏
     */
    private static String maskEnglishName(String name) {
        if (name.contains(" ")) {
            String[] parts = name.split("\\s+");
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < parts.length; i++) {
                if (i > 0) {
                    result.append(" ");
                }
                result.append(maskEnglishWord(parts[i]));
            }

            return result.toString();
        } else {
            return maskEnglishWord(name);
        }
    }

    /**
     * 脱敏单个英文单词
     */
    private static String maskEnglishWord(String word) {
        if (word == null || word.isEmpty()) {
            return "";
        }

        if (word.length() == 1) {
            return word;
        }

        char firstChar = word.charAt(0);
        StringBuilder mask = new StringBuilder();
        for (int i = 0; i < word.length() - 1; i++) {
            mask.append("*");
        }

        return firstChar + mask.toString();
    }

    @SuppressLint("HardwareIds")
    public static String getAndroidId(Context context) {
        String id = "";
        try {
            id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception ignored) {
        }
        return id;
    }

    public static int getChannel(Context context) {
        int channel = 0;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            channel = ai.metaData.getInt("CHANNEL_VALUE");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return channel;
    }

}

