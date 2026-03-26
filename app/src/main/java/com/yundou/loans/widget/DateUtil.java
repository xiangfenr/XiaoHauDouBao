package com.yundou.loans.widget;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

//import java.sql.Date;

/**
 * 日期工具
 *
 * @author fengkun
 */
@SuppressLint("SimpleDateFormat")
public class DateUtil {

    public static final String FORMAT_YMDHMS_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_YMDHMS_DEFAULT_OTHER = "yyyy/MM/dd HH:mm:ss";
    public static final String FORMAT_YMDHMS_DEFAULT_OTHER2 = "yyyy/MM/dd HH/mm/ss";
    public static final String FORMAT_YMDHM_DEFAULT = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_HMS_DEFAULT = "HH:mm:ss";
    public static final String FORMAT_HM_DEFAULT = "HH:mm";
    public static final String FORMAT_YMD_DEFAULT = "yyyy-MM-dd";
    public static final String FORMAT_YMD_ONEW = "yyyy/MM/dd";
    public static final String FORMAT_YMD_ONEW_China = "yyyy年MM月dd日";
    public static final String FORMAT_YMD_ONEW_TIME = "yyyy-MM-dd";
    public static final String FORMAT_YMD_China = "MM月dd日";
    public static final String FORMAT_YMD_POINT = "yyyy.MM.dd";
    public static final String FORMAT_YMD = "yyyyMMdd";
    public static final String FORMAT_YMD_HMS = "yyyyMMddHHmmss";
    public static final String FORMAT_YMD_YY = "yyyy";
    public static final String FORMAT_YMD_MM = "yyyy";
    public static final String FORMAT_YMD_dd = "yyyy";
    public static final String FORMAT_YYYY_MM = "yyyy/MM";
    public static final String FORMAT_YYYY_MM2 = "yyyy-MM";
    public static final String FORMAT_MD_DEFAULT = "MM-dd";
    public static final String FORMAT_AHM_DEFAULT = "aaaa hh:mm";
    public static final String FORMAT_MD_DEFAULT_MM = "MM";
    public static final String FORMAT_MD_DEFAULT_DD = "dd";

    private static String TAG = DateUtil.class.getName();

    /**
     * 格式
     */
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentDateTime() {

        long ld = System.currentTimeMillis();
        String date = df.format(new Date(ld));
        return date;
    }

    public static String getCurrentDate() {
        return getCurrentDate(FORMAT_YMDHMS_DEFAULT);
    }

    /**
     * 获取当前日期   格式 yyyy/MM/dd
     *
     * @return
     */
    public static String getCurrentDateOne() {
        return getCurrentDate(FORMAT_YMD_ONEW);
    }

    public static String getCurrentDateChina() {
        return getCurrentDate(FORMAT_YMD_ONEW_China);
    }

    public static String getCurrentMonthDay() {
        return getCurrentDate(FORMAT_MD_DEFAULT);
    }

    public static String getCurrentDate(String pattern) {
        long ld = System.currentTimeMillis();
        String date = new SimpleDateFormat(pattern).format(new Date(ld));
        return date;
    }

    /**
     * 获取昨天的日期，只能精确到天
     *
     * @return 昨天的日期
     */
    public static GregorianCalendar getYesterday() {
        long today = toLong(getCurrentDateTime(), FORMAT_YMD_DEFAULT);
        long day = 24 * 60 * 60 * 1000;//一天的毫秒数

        long yesterday = today - day;

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(yesterday);
        return gc;
    }

    /**
     * 把字符串类型的时间转化为long类型
     *
     * @param dateOrTime 字符串类型的时间
     * @param format     字符串时间对应的格式
     * @return 字符串时间对应的long类型时间
     */
    public static long toLong(String dateOrTime, String format) {
        if (dateOrTime == null) return -1;
        DateFormat df = new SimpleDateFormat(format);
        try {
            Date date = df.parse(dateOrTime);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String toString(GregorianCalendar calendar) {
        return toString(calendar, FORMAT_YMDHMS_DEFAULT_OTHER2);
    }

    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }


    /**
     * 把long类型的时间转化为字符串类型
     *
     * @param dateOrTime long类型的时间
     * @param format     时间对应的格式
     * @return long时间对应的字符串类型时间
     */
    public static String toString(long dateOrTime, String format) {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(new Date(dateOrTime));
    }

    public static String toString(Date date, String format) {
        try {
            DateFormat df = new SimpleDateFormat(format);
            return df.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String toString(GregorianCalendar calendar, String format) {
        long timeInMillis = calendar.getTimeInMillis();
        return toString(timeInMillis, format);
    }

    /**
     * 截取日期中的时分秒，格式为HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String getSubTime(String date) {
        String t = date;
        try {
            t = date.split(" ")[1];
        } catch (Exception e) {
            e.printStackTrace();
            return date;
        }
        return t;
    }

    /**
     * 截取日期中的年月日，格式为YY:MM:DD
     *
     * @param date
     * @return
     */
    public static String getSubDate(String date) {
        String t = date;
        try {
            t = date.split(" ")[0];
        } catch (Exception e) {
            e.printStackTrace();
            return date;
        }
        return t;
    }

    /**
     * 截取日期中的时分，格式为HH:mm
     *
     * @param date
     * @return
     */
    public static String getSubTime2(String date) {
        String t = getSubTime(date);
        t = t.substring(0, 5);
        return t;
    }

    /**
     * @param @param  dateString
     * @param @return 设定文件
     * @return Date    返回类型
     * @Title: stringToDate
     * @Description: string转换成date
     */
    public static Date stringToDate(String dateString) {
        Date dateValue = null;
        try {
            dateValue = df.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateValue;
    }


    public static String secondToTime(float n) {
        int hr = (int) (n / 60 / 60);
        int mt = (int) ((n - hr * 60 * 60) / 60);
        int sec = (int) (n - hr * 60 * 60 - mt * 60);

        String hrStr = hr + ":";
        String mtStr = mt + ":";
        String secStr = sec + "";
        if (hr < 10) {
            hrStr = "0" + hr + ":";
        }

        if (mt < 10) {
            mtStr = "0" + mt + ":";
        }

        if (sec < 10) {
            secStr = "0" + secStr;
        }

        return hrStr + mtStr + secStr;
    }


    /**
     * 获取指定日期的上一日日期
     *
     * @param calendar 指定的日期
     * @return 上一日的日期
     */
    public static GregorianCalendar getPreviousDay(GregorianCalendar calendar) {
        GregorianCalendar result = DateUtil.cloneCalendar(calendar);
        long temp = calendar.getTimeInMillis() - (24 * 60 * 60 * 1000);
        result.setTimeInMillis(temp);
        return result;
    }


    /**
     * 获取指定日期的上个月日期
     *
     * @param calendar 指定的日期
     * @return 上个月的日期
     */
    public static GregorianCalendar getPreviousMonth(GregorianCalendar calendar) {
        GregorianCalendar result = DateUtil.cloneCalendar(calendar);
        int m = calendar.get(Calendar.MONTH);
        if (m == GregorianCalendar.JANUARY) {
            result.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
            result.set(Calendar.MONTH, GregorianCalendar.DECEMBER);
        } else {
            result.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        }
        return result;
    }

    public static GregorianCalendar cloneCalendar(GregorianCalendar calendar) {
        GregorianCalendar result = (GregorianCalendar) GregorianCalendar.getInstance();
        result.setTimeInMillis(calendar.getTimeInMillis());
        return result;
    }

    public static void resetMonth(GregorianCalendar tempItem) {
        tempItem.set(Calendar.DAY_OF_MONTH, 1);
        tempItem.set(Calendar.HOUR, 0);
        tempItem.set(Calendar.MINUTE, 0);
        tempItem.set(Calendar.SECOND, 0);
    }

    /**
     * 把时间秒转换成小时
     *
     * @param second
     * @return
     */
    public static float secondToHour(long second) {
        return second / 3600.0f;
    }

    /**
     * 把时间秒转换毫秒
     *
     * @param second
     * @return
     */
    public static long secondToMs(long second) {
        return second * 1000;
    }


    /**
     * 秒转00:00:00
     *
     * @param l
     * @return
     */
    public static String formatLongToTimeStr(Long l) {
        int hour = 0;
        int minute = 0;
        int second = 0;

        second = l.intValue();

        if (second > 60) {
            minute = second / 60;
            second = second % 60;
        }
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        return (getTwoLength(hour) + ":" + getTwoLength(minute) + ":" + getTwoLength(second));
    }


    /**
     * 获取给定日期的一年后前一秒的日期
     *
     * @param startTime
     * @param format
     * @return
     */
    public static GregorianCalendar getTodayOfNextYear(String startTime, String format) {
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        long t = toLong(startTime, format);
        calendar.setTimeInMillis(t);

        calendar.add(Calendar.YEAR, 1);
        calendar.add(Calendar.SECOND, -1);
        return calendar;
    }

    public static String getTodayStrOfNextYear(String startTime, String format) {
        GregorianCalendar calendar = getTodayOfNextYear(startTime, format);
        return DateUtil.toString(calendar, format);
    }

    private static String getTwoLength(final int data) {
        if (data < 10) {
            return "0" + data;
        } else {
            return "" + data;
        }
    }

    public static Calendar toCalendar(String dateStr, String format) {
        Date date = toDate(dateStr, format);
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        return instance;
    }

    @Nullable
    public static Date toDate(String dateStr, String format) {
        if (!TextUtils.isEmpty(dateStr)) {
            SimpleDateFormat fmt = new SimpleDateFormat(format);
            try {
                return fmt.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static int getAgeDifference(Date newDate, Date oldDate, String type) {
        if (newDate == null || oldDate == null) {
            return -1;
        }
        Calendar cal_app = Calendar.getInstance();
        Calendar cBirth = Calendar.getInstance();
        if (cBirth.before(newDate)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        cal_app.setTime(newDate);
        int yearNow = cal_app.get(Calendar.YEAR);
        int monthNow = cal_app.get(Calendar.MONTH);
        int dayOfMonthNow = cal_app.get(Calendar.DAY_OF_MONTH);
        cBirth.setTime(oldDate);
        int year_apply = cBirth.get(Calendar.YEAR);
        int month_apply = cBirth.get(Calendar.MONTH);
        int dayOfMonth_apply = cBirth.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - year_apply;
        if (monthNow <= month_apply) {
            //如果月份相等，在比较日期，如果当前日，小于投保日，也减1，表示不满多少周岁
            if (monthNow == month_apply) {
                if (dayOfMonthNow < dayOfMonth_apply) {
                    if (!type.equals("expiration_date")) {
                        age--;
                    }
                } else if (dayOfMonthNow > dayOfMonth_apply && type.equals("expiration_date")) {
                    age++;
                }
            } else {
                if (!type.equals("expiration_date")) {
                    age--;
                }
            }
        } else if (type.equals("expiration_date")) {
            age++;
        }
        return age;
    }

    public static int getAgeNew(String apply_day, String birDay) {
        //投保年龄
        int buyAge = getAgeDifference(stringToDateMe(apply_day, "yyyy-MM-dd"), stringToDateMe(birDay, "yyyy-MM-dd"), "");

        //当前年龄
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        int curAge = getAgeDifference(stringToDateMe(simpleDateFormat.format(date), "yyyy-MM-dd"), stringToDateMe(birDay, "yyyy-MM-dd"), "");
        return curAge - buyAge;
    }

    /**
     * 调此方法输入所要转换的时间输入例如（"2014-06-14-16-09-00"）返回时间戳
     *
     * @param time
     * @return
     */
    public static String dataOne(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss",
                Locale.CHINA);
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            String stf = String.valueOf(l);
            times = stf.substring(0, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }

    public static int inday(Date brithday) {
        Calendar cToday = Calendar.getInstance(); // 存今天
        Calendar cBirth = Calendar.getInstance(); // 存生日
        cBirth.setTime(brithday); // 设置生日
        cBirth.set(Calendar.YEAR, cToday.get(Calendar.YEAR)); // 修改为本年
        int days;
        if (cBirth.get(Calendar.DAY_OF_YEAR) < cToday.get(Calendar.DAY_OF_YEAR)) {
            // 生日已经过了，要算明年的了
            days = cToday.getActualMaximum(Calendar.DAY_OF_YEAR) - cToday.get(Calendar.DAY_OF_YEAR);
            days += cBirth.get(Calendar.DAY_OF_YEAR);
        } else {
            // 生日还没过
            days = cBirth.get(Calendar.DAY_OF_YEAR) - cToday.get(Calendar.DAY_OF_YEAR);
        }
        // 输出结果
        return days;
    }

    /**
     * 默认时区为北京时间
     */
    public static TimeZone DEFAULT_SERVER_TIME_ZONE = TimeZone.getTimeZone("GMT+08:00");

    public static int getDaysDistance(Date apply_date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(apply_date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        // 输入指定日期
        long timeMillis = getTimeInMillis(year, month, day);
        Calendar current = Calendar.getInstance(TimeZone.getDefault());
        current.setTimeInMillis(System.currentTimeMillis());

        Calendar date = Calendar.getInstance(TimeZone.getDefault());
        date.setTimeInMillis(timeMillis);
        long time = current.getTimeInMillis() - date.getTimeInMillis();
        // 天
        return Math.round(time / 1000 / 60 / 60 / 24);
    }

    /**
     * 设置年、月、日、时、分、秒，并转换成时间戳
     */
    public static long getTimeInMillis(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(DEFAULT_SERVER_TIME_ZONE);
        calendar.set(year, month - 1, day);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        format.setTimeZone(DEFAULT_SERVER_TIME_ZONE);
        try {
            return format.parse(format.format(calendar.getTime())).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static Boolean checkStartEnd(String start, String end) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = simpleDateFormat.parse(start);
            Date date2 = simpleDateFormat.parse(end);
            //1.使用Date的compareTo()方法，大于、等于、小于分别返回1、0、-1
            if (date1.compareTo(date2) == -1) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static Boolean checkEndDay(String end) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = Calendar.getInstance().getTime();
            Date date2 = simpleDateFormat.parse(end);
            //1.使用Date的compareTo()方法，大于、等于、小于分别返回1、0、-1
            if (date1.compareTo(date2) == -1) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 时间戳转日期
     *
     * @param time    1541569323155
     * @param pattern yyyy-MM-dd HH:mm:ss
     * @return 2018-11-07 13:42:03
     */
    public static String getDate2String(long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        return format.format(date);
    }

    /**
     * 时间戳转日期
     *
     * @param time    20201231
     * @return 2018-11-07 13:42:03
     */
    public static String getDate2String(String time) {
        if (TextUtils.isEmpty(time)) return "";
        String longDate = "";
        try {
            Date format1 = new SimpleDateFormat("yyyyMMdd").parse(time);
            longDate = new SimpleDateFormat("yyyy-MM-dd").format(format1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return longDate;
    }

    //String 转化为 date
    public static Date stringToDateMe(String strTime, String formatType) {
        if (TextUtils.isEmpty(strTime)) {
            return null;
        }
        Date date = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(formatType);
            date = formatter.parse(strTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获得指定日期的前一天
     *
     * @param specifiedDay
     * @return
     * @throws Exception
     */
    public static String getSpecifiedDayBefore(String specifiedDay) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        c.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR)); // 修改为本年
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - 1);
        String dayBefore = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
        return dayBefore;
    }

    /**
     * 获得指定日期的后一天
     *
     * @param specifiedDay
     * @return
     * @throws Exception
     */
    public static String getSpecifiedDayAfter(String specifiedDay) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        c.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR)); // 修改为本年
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + 1);
        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        return dayBefore;
    }

    public static String StringToDate(String time) {
        if (TextUtils.isEmpty(time)) return null;
        Date date = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) return null;
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String s = format1.format(date);
        return s;
    }


    public static String StringToChinaDate(String time) {
        if (TextUtils.isEmpty(time)) return null;
        Date date = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) return null;
        SimpleDateFormat format1 = new SimpleDateFormat(FORMAT_YMD_China);
        String s = format1.format(date);
        return s;
    }

    public static Calendar stringToCalendar(String time) {
        if (TextUtils.isEmpty(time)) return null;
        if ("9999-12-31".equals(time)) return null;
        Calendar calendar = null;
        try {
            calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(time);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    /**
     * 将字符串类型时间转换为指定格式的字符串类型时间
     *
     * @param timeString   需要转换的字符串日期
     * @param timeFormat   当前需要转换的字符串日期的格式 例如 yyyy-MM-dd
     * @param targetFormat 转换后的日期的格式
     * @return
     */
    public static String toString(String timeString, String timeFormat, String targetFormat) {
        if (TextUtils.isEmpty(timeString))
            return "";
        SimpleDateFormat format = new SimpleDateFormat(timeFormat, Locale.CHINA);
        Date date;
        try {
            date = format.parse(timeString);
            SimpleDateFormat format1 = new SimpleDateFormat(targetFormat, Locale.CHINA);
            if (date != null)
                return format1.format(date);
            else
                return "";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(Date date1, Date date2) {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
        return days;
    }
}
