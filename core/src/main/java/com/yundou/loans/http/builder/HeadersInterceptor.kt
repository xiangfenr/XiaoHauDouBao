package com.yundou.loans.http.builder


import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.yundou.loans.base.BaseApp
import com.yundou.loans.utils.Constants
import com.yundou.loans.utils.LogUtils
import com.yundou.loans.utils.MmkvUtil
import com.yundou.loans.utils.SHA256
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.URLEncoder


class HeadersInterceptor : Interceptor {
    private val commonHeaders: LinkedHashMap<String, String?> = LinkedHashMap()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var original = chain.request()
        val baseURl = original.url.toString()
        val headerMap = hashMapOf<String, String?>()
        val builder = original.newBuilder()
        headerMap.putAll(commonHeaders)
        try {
            Log.e("HeadersInterceptor", "--------url ： ${original.url} ------------------")

            if (baseURl.contains("hapi.srloan.cn")) {  //笙融
                headerMap["ChannelCode"] = Constants.SR_CHANNEL
                headerMap["Platform"] = Constants.SR_Platform
                headerMap["PackageName"] = BaseApp.context.srpackage_id
            } else if (baseURl.contains("zhixiangjinfu")) { //智享贷

            } else if (baseURl.contains("app.beihua.site")) { //快易贷

            } else if (baseURl.contains("hyj.mlffgg.cn")) {  //魔力

            } else if (baseURl.contains(BaseApp.context.twoHeRuiUrl)) {  //二项目

                headerMap["channel"] = BaseApp.context.storeid.toString()
                headerMap["aid"] = BaseApp.context.app_id
                headerMap["t"] = (System.currentTimeMillis() / 1000).toString()

            } else if (baseURl.contains(BaseApp.context.jiYongBangUrl)) {  //吉用帮
                headerMap["distributorId"] = Constants.JYB_distributorId
            } else if (baseURl.contains(BaseApp.context.tianxiaFenQiBaseUrl)) {
                headerMap["brand"] = "0"
                headerMap["X-Requested-With"] = "XMLHttpRequest"
            } else if (baseURl.contains(BaseApp.context.yuanXiaoHuaBaseUrl)) {

                val md5 =
                    Constants.YXH_APP_ID + Constants.YXH_PACKAGE_NAME + Constants.YXH_VERSION + Constants.YXH_NONCE + Constants.YXH_SECRET
                val sign = SHA256.encryptMD5(md5)
                headerMap["Authorization"] = Constants.TEST_TOKEN
                headerMap["Kkh-Package-Name"] = Constants.YXH_PACKAGE_NAME
                headerMap["Kkh-Version"] = Constants.YXH_VERSION
                headerMap["Kkh-Appid"] = Constants.YXH_APP_ID
                headerMap["Kkh-Sign"] = sign
                headerMap["Kkh-Nonce"] = Constants.YXH_NONCE
            } else {
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

//                if (!TextUtils.isEmpty(androidId)) {
//                    headerMap["oaid"] = androidId
//                } else {
//                    val currentTimestampMillis = System.currentTimeMillis()
//                    val currentTimestampSeconds: Long = currentTimestampMillis / 1000
//                    headerMap["oaid"] = currentTimestampSeconds.toString()
//                    androidId = currentTimestampSeconds.toString()
//                }
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
                LogUtils.i("Header sign param: $signStr")
                // 生成签名
                val sign = SHA256.encryptMD5(SHA256.encryptMD5(signStr))

                headerMap["sign"] = sign
                headerMap.run {
                    for (key in keys) {
                        builder.removeHeader(key)
                        this[key]?.let { builder.addHeader(key, it) }
                    }
                }

                original = builder.build()
                val skey = hashMapOf<String, String?>()
                skey["secret"] = original.headers["secret"]
                skey["app_version"] = original.headers["app_version"]
                skey["timestamp"] = original.headers["timestamp"]
                original = builder.build()
                Log.e("HeadersInterceptor", "header： ${original.headers}")
                Log.e("HeadersInterceptor", " skey: ${Gson().toJson(skey)}")
            }

            val partner_id = MmkvUtil.getInstance().decodeInt("partner_id")
            val token = MmkvUtil.getInstance().decodeString("token")

            Log.d("AAAAA", "intercept: token -- > " + token)

            if (!TextUtils.isEmpty(token)) {
                when (partner_id) {
                    Constants.PARTNER_BENBU, Constants.PARTNER_TWOP, Constants.PARTNER_JIDAI, Constants.PARTNER_YUEXIANG -> { //本部Token
                        headerMap["token"] = token
                    }

                    Constants.PARTNER_SHENGR, Constants.PARTNER_YANGXINHUA -> { //笙融Token,阳薪花Token
                        headerMap["Token"] = token
                    }

                    Constants.PARTNER_ZXD, Constants.PARTNER_JIYONGBANG -> { // //智享贷Token ,吉用帮ToKen
                        headerMap["Authorization"] = token
                    }

                    Constants.PARTNER_JIYONGQB -> {
                        headerMap["Authorization"] = "Bearer $token"  //吉用钱包
                    }

                    Constants.PARTNER_YOUQIANQB -> {
                        headerMap["accessToken"] = token
                    }

                    Constants.PARTNER_TXFQ -> {
                        headerMap["authorization"] = token
                    }

                    Constants.PARTNER_YUANXIAOHUA -> { //源小花
                        headerMap["Authorization"] = "Bearer $token"
                    }
                }
            }
            //很重要--给合作平台用来区分发送短信的
//            headerMap["wmySmsChannel"] = Constants.MYSMS_CHANNEL

            headerMap.run {
                for (key in keys) {
                    builder.removeHeader(key)
                    this[key]?.let { builder.addHeader(key, it) }
                }
            }

            original = builder.build()
            Log.e("HeadersInterceptor", "*****header****： ${original.headers}")

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

