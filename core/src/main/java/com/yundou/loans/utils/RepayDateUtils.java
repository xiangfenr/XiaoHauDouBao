package com.yundou.loans.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.yundou.loans.base.BaseApp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @Author: fenr
 * 时间: 2025/2/5
 * 类名: ACTIVITY
 * 简述:  还款日期的工具类
 */
public class RepayDateUtils {


    private static final String PREFS_NAME = "AppPreferences";
    private static final String FIRST_LAUNCH_DATE_KEY = BaseApp.context.getVersion();


    // 获取加25天后的时间并格式化
    public static String getDateAfter25Days(String currentDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(currentDate);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, 27);

            return sdf.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 第一次打开App时获取并保存当前时间
    public static String getFirstLaunchDate(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String firstLaunchDate = prefs.getString(FIRST_LAUNCH_DATE_KEY, null);

        if (firstLaunchDate == null) {
            firstLaunchDate = getCurrentFormattedDate();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(FIRST_LAUNCH_DATE_KEY, firstLaunchDate);
            editor.apply();
        }

        return firstLaunchDate;
    }

    public static String getCurrentFormattedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -6); // Subtract 5 days from the current date
        return dateFormat.format(calendar.getTime());
    }
    public static String getCurrentFormatDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        return dateFormat.format(new Date());
    }

}
