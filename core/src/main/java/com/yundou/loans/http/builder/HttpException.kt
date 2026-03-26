package com.yundou.loans.http.builder

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.yundou.loans.http.ResultBean
import org.json.JSONException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException
import javax.net.ssl.SSLException

class HttpException : Exception() {

    private var e: Throwable? = null
    private var code: String? = null
    private var msg: String? = null

    companion object {
        val instance: HttpException by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            HttpException()
        }
    }

    fun handle(e: Throwable?): HttpException? {
        this.e = e
        code = onGetCode(e)
        msg = onGetMsg(e, code!!)
        return instance
    }

    protected fun onGetCode(e: Throwable?): String {
        return if (e == null) {
            Code.NET
        } else {
            if (e is SocketTimeoutException) {
                Code.TIMEOUT
            } else if (e is retrofit2.HttpException) {
                Code.HTTP
            } else if (e is UnknownHostException || e is ConnectException) {
                Code.HOST
            } else if (e is JsonParseException || e is ParseException || e is JSONException) {
                Code.JSON
            } else if (e is SSLException) {
                Code.SSL
            } else if (e is retrofit2.adapter.rxjava2.HttpException && e.code() == 410) {
                Code.DOWNLOAD_POLICY_FILE_NOT_FOUND
            } else {
                Code.UNKNOWN
            }
        }
    }

    protected fun onGetMsg(e: Throwable?, code: String): String? {
        val msg: String =
            when (code) {
                Code.NET -> "网络连接失败，请检查网络设置"
                Code.TIMEOUT -> "网络状况不稳定，请稍后重试"
                Code.JSON -> "JSON解析异常"
                Code.HTTP -> {
                    try {
                        var resultBean: ResultBean<Any>? = null
                        val gson = Gson()
                        val result =
                            (e as retrofit2.HttpException).response()?.errorBody()?.string()
                        if (!result.isNullOrEmpty()) {
                            resultBean = gson.fromJson(
                                result,
                                object : TypeToken<ResultBean<Any>>() {}.type
                            )
                            if (resultBean != null && !resultBean.getMsg().isNullOrEmpty())
                                resultBean.getMsg()
                            else
                                "请求错误，请稍后重试"
                        } else {
                            "加载数据返回错误"
                        }
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                        "JSON解析异常"
                    }
                }

                Code.HOST -> "服务器连接失败，请检查网络设置"
                Code.SSL -> "登录失效"
                Code.LOGINOUT -> "登录失效"
                Code.DOWNLOAD_POLICY_FILE_NOT_FOUND -> "您的电子保单我们在努力生成中。"
                else -> "未知错误，请稍后重试"
            }
        return msg
    }

    fun getCode(): String? = code
    fun getMsg(): String? = msg
    fun getException(): Throwable? = e

    interface Code {
        companion object {
            const val UNKNOWN = "-1"
            const val NET = "0"
            const val TIMEOUT = "1"
            const val JSON = "2"
            const val HTTP = "3"
            const val HOST = "4"
            const val SSL = "5"
            const val LOGINOUT = "6"
            const val DOWNLOAD_POLICY_FILE_NOT_FOUND = "7"
        }
    }
}

