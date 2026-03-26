package com.yundou.loans.exceptions

import android.util.Log
import com.google.gson.JsonParseException
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException
import javax.net.ssl.SSLHandshakeException

/**
 * 异常处理
 */
object ExceptionHandler {
    private const val HTTP = "http"

    fun handleException(e: Throwable): ApiException {
        when (e) {
            //服务器返回错误
            is ServerException -> {
                Log.e(HTTP, "服务器返回错误")
                return ApiException(e, e.code, e.msg)
            }
            //HTTP状态错误
            is HttpException -> {
                Log.e(HTTP, "网络错误")
                return ApiException(e, ApiException.HTTP_ERROR, "网络错误")
            }
            //解析错误
            is JsonParseException,
            is JSONException,
            is ParseException -> {
                Log.e(HTTP, "解析错误")
                return ApiException(e, ApiException.PARSE_ERROR, "解析错误")
            }
            //网络错误
            is ConnectException,
            is UnknownHostException,
            is ConnectTimeoutException,
            is SocketTimeoutException -> {
                Log.e(HTTP, "网络连接失败")
                return ApiException(e, ApiException.NETWORK_ERROR, "网络连接失败")
            }
            //证书验证失败
            is SSLHandshakeException -> {
                Log.e(HTTP, "证书验证失败")
                return ApiException(e, ApiException.SSL_ERROR, "证书验证失败")
            }
            //未知错误
            else -> {
                Log.e(HTTP, "未知错误")
                return ApiException(e, ApiException.UNKNOWN, "未知错误")
            }
        }
    }

}





