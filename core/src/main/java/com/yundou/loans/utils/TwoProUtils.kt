package com.yundou.loans.utils


import com.yundou.loans.entity.TwoPFormData
import java.text.SimpleDateFormat
import java.util.*
import java.net.URL
import kotlinx.coroutines.*
import org.json.JSONObject

/**
 * @Author: fenr
 * 时间: 2025/3/27
 * 类名: ACTIVITY
 * 简述:
 *
 */
object TwoProUtils {


    fun getAgeAndGender(idCard: String): Pair<Int?, Int?> {
        if (idCard.length != 18) return Pair(null, null)  // 校验身份证长度

        try {
            // 获取出生日期（身份证第6-14位）
            val birthYear = idCard.substring(6, 10).toInt()
            val birthMonth = idCard.substring(10, 12).toInt()
            val birthDay = idCard.substring(12, 14).toInt()

            // 计算年龄
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH) + 1
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

            var age = currentYear - birthYear
            if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
                age-- // 如果生日未过，年龄减一
            }

            // 计算性别（身份证倒数第二位）
            val genderCode = idCard.substring(16, 17).toInt()
            val gender = if (genderCode % 2 == 0) 2 else 1 // 偶数是女性，奇数是男性

            return Pair(age, gender)

        } catch (e: Exception) {
            e.printStackTrace()
            return Pair(null, null) // 捕获异常返回 null
        }
    }


    fun getPublicIp(defaultIp: String = "192.168.1.1", callback: (String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = URL("https://api.ipify.org?format=json").readText()
                val ip = JSONObject(response).getString("ip")
                withContext(Dispatchers.Main) {
                    callback(ip)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback(defaultIp)  // 发生异常时返回默认 IP
                }
            }
        }
    }



}