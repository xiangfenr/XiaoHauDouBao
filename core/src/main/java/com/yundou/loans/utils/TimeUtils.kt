package com.yundou.loans.utils

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {

    //获取当前时间 年月日
    fun getTime(): String {
        //年月日
        val yaer = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH) + 1
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val stringBuilder = StringBuilder()
        stringBuilder.append(yaer.toString() + "年  ")
        stringBuilder.append(month.toString() + "月")
        stringBuilder.append(day.toString() + "日  ")
        stringBuilder.append(getWeek())
        return stringBuilder.toString()
    }

    private fun getWeek(): String {
        val cal = Calendar.getInstance()
        val i = cal[Calendar.DAY_OF_WEEK]
        return when (i) {
            1 -> "周日"
            2 -> "周一"
            3 -> "周二"
            4 -> "周三"
            5 -> "周四"
            6 -> "周五"
            7 -> "周六"
            else -> ""
        }
    }


    //获取当前时间 年月日
    fun getLocationTime(): StringBuilder {
        var newMonth: String? = null
        var newDay: String? = null

        //年月日
        val yaer = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH) + 1
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        //查询打卡记录
        val searDateBuilder = StringBuilder()
        newMonth = if (month < 10) {
            "0$month"
        } else {
            month.toString()
        }

        newDay = if (day < 10) {
            "0$day"
        } else {
            day.toString()
        }
        searDateBuilder.append("$yaer-").append("$newMonth-").append("$newDay")
        return searDateBuilder
    }

    fun getFormat(): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }


    fun getWeekDay(week: String?): String {
        return when (week) {
            "日" -> "0"
            "一" -> "1"
            "二" -> "2"
            "三" -> "3"
            "四" -> "4"
            "五" -> "5"
            "六" -> "6"
            else -> ""
        }
    }

    fun getTimeDay(week: String?): String {
        return when (week) {
            "10/分钟" -> "10"
            "20/分钟" -> "20"
            "30/分钟" -> "30"
            else -> ""
        }
    }

}