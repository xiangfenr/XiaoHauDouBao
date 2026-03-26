package com.yundou.loans.utils

import android.content.Context
import android.net.wifi.WifiManager
import java.net.HttpURLConnection
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.URL
import kotlin.io.bufferedReader
import kotlin.io.readText
import kotlin.io.use
import kotlin.let
import kotlin.text.format

/**
 * @Author: fenr
 * 时间: 2025/9/17
 * 类名: IpUtils
 * 简述: IP 地址获取
 *
 */
object IpUtils {

    /**
     * 获取当前设备 IP（Wi-Fi 或 移动网络）
     */
    fun getDeviceIp(context: Context): String? {
        // 1. 先尝试 Wi-Fi
        getWifiIp(context)?.let {
            return it
        }
        // 2. 再尝试移动网络/以太网
        return getLocalIpAddress()
    }

    /**
     * 获取 Wi-Fi 下的 IP
     */
    private fun getWifiIp(context: Context): String? {
        return try {
            val wifiManager =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            val ipInt = wifiInfo.ipAddress
            if (ipInt != 0) {
                String.format(
                    "%d.%d.%d.%d",
                    ipInt and 0xff,
                    ipInt shr 8 and 0xff,
                    ipInt shr 16 and 0xff,
                    ipInt shr 24 and 0xff
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 获取移动数据/以太网 IP
     */
    private fun getLocalIpAddress(): String? {
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val intf = interfaces.nextElement()
                val addrs = intf.inetAddresses
                while (addrs.hasMoreElements()) {
                    val addr = addrs.nextElement()
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        return addr.hostAddress
                    }
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 获取公网 IP（需要联网，调用外部服务）
     * ⚠️ 建议放在协程或后台线程中调用
     */
    fun getPublicIp(): String? {
        return try {
            val url = URL("https://api.ipify.org")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 3000
            conn.readTimeout = 3000
            conn.inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            null
        }
    }
}