package com.yundou.loans.http.builder


import android.text.TextUtils
import android.util.Log
import com.yundou.loans.base.BaseApp
import com.yundou.loans.utils.Constants
import com.yundou.loans.utils.MmkvUtil
import com.yundou.loans.utils.SHA256
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.URLEncoder


/**
 * 公共请求头拦截器。
 * 当前版本仅保留本部服务与魔力合作方，其余渠道的分支已移除。
 */
class HeadersInterceptor : Interceptor {
    private val commonHeaders: LinkedHashMap<String, String?> = LinkedHashMap()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var original = chain.request()
        val headerMap = hashMapOf<String, String?>()
        val builder = original.newBuilder()
        headerMap.putAll(commonHeaders)
        try {
            headerMap["v"] = BaseApp.context.version
            headerMap["aid"] = BaseApp.context.app_id
            val time = (System.currentTimeMillis() / 1000).toString()
            headerMap["t"] = time
            var androidId = MmkvUtil.getInstance().decodeString("androidId")
            var oaid = MmkvUtil.getInstance().decodeString("oaid")

            if (!TextUtils.isEmpty(oaid)) {
                headerMap["oaid"] = oaid
            } else {
                if (!TextUtils.isEmpty(androidId)) {
                    oaid = androidId
                    headerMap["oaid"] = androidId
                } else {
                    val currentTimestampMillis = System.currentTimeMillis()
                    val currentTimestampSeconds: Long = currentTimestampMillis / 1000
                    oaid = currentTimestampSeconds.toString()
                    headerMap["oaid"] = currentTimestampSeconds.toString()
                }
            }

            headerMap["sid"] = BaseApp.context.storeid.toString() //应用市场

            val params = mutableMapOf<String, String>()
            params["aid"] = BaseApp.context.app_id
            params["oaid"] = oaid.toString()
            params["sid"] = BaseApp.context.storeid
            params["t"] = time.toString()
            params["v"] = BaseApp.context.version
            val sortedMap = params.toSortedMap()
            val sb = StringBuilder()
            sortedMap.forEach { (key, value) ->
                if (sb.isNotEmpty()) sb.append("&")
                sb.append(key)
                    .append("=")
                    .append(URLEncoder.encode(value, "UTF-8"))
            }
            val secret = "y1sHeng"
            sb.append(secret)
            val signStr = sb.toString()
            // 生成签名
            val sign = SHA256.encryptMD5(SHA256.encryptMD5(signStr))

            headerMap["sign"] = sign

            val partner_id = MmkvUtil.getInstance().decodeInt("partner_id")
            val token = MmkvUtil.getInstance().decodeString("token")

            if (!TextUtils.isEmpty(token) && partner_id == Constants.PARTNER_BENBU) {
                headerMap["token"] = token
            }

            headerMap.run {
                for (key in keys) {
                    builder.removeHeader(key)
                    this[key]?.let { builder.addHeader(key, it) }
                }
            }

            original = builder.build()

        } catch (e: Exception) {
            Log.e("HeadersInterceptor", chain.request().url.toString())
            e.printStackTrace()
        }
        return chain.proceed(original)
    }

    fun putHeaders(headers: Map<String, String?>?) {
        this.commonHeaders.clear()
        if (headers != null) {
            this.commonHeaders.putAll(headers)
        }
    }
}
