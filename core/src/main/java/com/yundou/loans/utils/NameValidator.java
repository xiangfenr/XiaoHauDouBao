package com.yundou.loans.utils;

public class NameValidator {
    /**
     * 验证中文姓名
     * 支持中文姓名，包含少数民族姓名中的点
     */
    public static boolean isValidChineseName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        // 正则表达式：支持中文、少数民族姓名中的点·
        String regex = "^[\\u4e00-\\u9fa5]{2,20}(·[\\u4e00-\\u9fa5]{2,20})?$";
        return name.matches(regex);
    }

    /**
     * 验证英文姓名
     * 支持英文姓名，包含空格和点
     */
    public static boolean isValidEnglishName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        // 正则表达式：支持英文、空格、点
        String regex = "^[a-zA-Z\\s.'-]{2,50}$";
        return name.matches(regex);
    }

    /**
     * 通用姓名验证（中英文都支持）
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        // 去除首尾空格
        name = name.trim();

        // 检查长度
        if (name.length() < 2 || name.length() > 50) {
            return false;
        }

        // 同时支持中英文的验证规则
        String regex = "^[\\u4e00-\\u9fa5a-zA-Z\\s.'·-]+$";
        return name.matches(regex);
    }
}
