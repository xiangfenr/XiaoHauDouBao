package com.yundou.loans.utils;

import android.util.Log;

import java.io.Closeable;
import java.io.FileOutputStream;

public class LogUtils {
    private static FileOutputStream fos;
    public static String tag = "picc_rbgj";
    public static boolean isDebug = true;
    private static LogUtils instance;

    public static void i(String tag, String str) {
        print(Log.INFO, tag, str);
    }

    public static void d(String tag, String str) {
        print(Log.DEBUG, tag, str);
    }

    public static void v(String tag, String str) {
        print(Log.VERBOSE, tag, str);
    }

    public static void w(String tag, String str) {
        print(Log.WARN, tag, str);
    }

    public static void e(String tag, String str) {
        print(Log.ERROR, tag, str);
    }

    public LogUtils newInstance() {
        if (instance == null) {
            new LogUtils();
        }
        return instance;
    }

    public void init(boolean isDebug, String tag) {
        this.isDebug = isDebug;
        this.tag = tag;
    }

    public static void i(String str) {
        print(Log.INFO, str);
    }

    public static void d(String str) {
        print(Log.DEBUG, str);
    }

    public static void v(String str) {
        print(Log.VERBOSE, str);
    }

    public static void w(String str) {
        print(Log.WARN, str);
    }

    public static void e(String str) {
        print(Log.ERROR, str);
    }

    private static void print(int index, String msg) {
        print(index, tag, msg);
    }

    private static void print(int index, String tag, String msg) {
        if (msg == null || msg.length() == 0) return;
        int segmentSize = 3 * 1024;
        long length = msg.length();
        if (length <= segmentSize) {
            checkMsg(index, tag, msg);
        } else {
            while (msg.length() > segmentSize) {
                String logContent = msg.substring(0, segmentSize);
                msg = msg.replace(logContent, "");
                checkMsg(index, tag, logContent);
            }
            checkMsg(index, tag, msg);
        }
    }

    private static void checkMsg(int index, String tag, String msg) {
        if (!isDebug) return;
        switch (index) {
            case Log.VERBOSE:
                Log.v(tag, msg);
                break;
            case Log.DEBUG:
                Log.d(tag, msg);
                break;
            case Log.INFO:
                Log.i(tag, msg);
                break;
            case Log.WARN:
                Log.w(tag, msg);
                break;
            case Log.ERROR:
                Log.e(tag, msg);
                break;
            default:
                break;
        }
    }

    public static String getLogFileName() {
        return "requestLog.log";
    }

    protected static void closeSilently(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable e) {
                // ignored
            }
        }
    }
}

