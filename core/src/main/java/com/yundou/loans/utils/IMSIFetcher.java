package com.yundou.loans.utils;/*
 *@author jh
 *create at $
 *description:
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

public class IMSIFetcher {

    @SuppressLint("HardwareIds")
    public static String getIMSI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            return telephonyManager.getSubscriberId();
        }
        return "";
    }


    @SuppressLint("HardwareIds")
    public String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            return telephonyManager.getDeviceId();
        }
        return "";
    }

}
