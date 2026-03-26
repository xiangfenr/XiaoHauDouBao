package com.yundou.loans.base

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yundou.loans.http.ResultBean
import com.yundou.loans.utils.LogUtils
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException

object ExceptionHandle {

    fun handleException(e: Throwable): ResponseThrowable {
        LogUtils.e("handleException1  :${e.localizedMessage}")
        val ex: ResponseThrowable

        if (e is ResponseThrowable) {
            ex = e
        } else if (e is HttpException) {
            val response = e.response()
            val result = response?.errorBody()?.string()
            val url = response?.raw().toString()

            if (e.code() == 500) return ResponseThrowable(
                "${e.code()}",
                ERROR.HTTP_ERROR.getValue(),
                e,
                url.toString()
            )
            LogUtils.e("handleException2  :$result")

            if (!result.isNullOrEmpty()) {
                var resultBean: ResultBean<Any>? = null
                try {
                    val gson = Gson()
                    resultBean = gson.fromJson(
                        result,
                        object : TypeToken<ResultBean<Any>>() {}.type
                    )

                    // 登录失效：只发事件，交给壳处理跳转
                    if (resultBean?.code == 401) {
                        EventCenter.postNeedLogin()
                    }

                    resultBean?.msg?.let { msg ->
                        return ResponseThrowable(
                            "${e.code()}",
                            msg,
                            e,
                            url.toString(),
                            resultBean?.traceId
                        )
                    }
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }
                ex = ResponseThrowable(
                    "${e.code()}",
                    ERROR.HTTP_ERROR.getValue(),
                    e,
                    url.toString(),
                    resultBean?.traceId
                )
            } else {
                ex = ResponseThrowable(
                    "${e.code()}",
                    ERROR.HTTP_ERROR.getValue(),
                    e,
                    url.toString()
                )
            }
        } else if (e is JSONException || e is android.net.ParseException) {
            ex = ResponseThrowable(ERROR.PARSE_ERROR, e)
        } else if (e is ConnectException) {
            ex = ResponseThrowable(ERROR.NETWORD_ERROR, e)
        } else if (e is javax.net.ssl.SSLException) {
            ex = ResponseThrowable(ERROR.SSL_ERROR, e)
        } else if (e is NullPointerException) {
            ex = ResponseThrowable(ERROR.NULL_ERROR, e)
        } else if (e is IllegalArgumentException) {
            ex = ResponseThrowable(ERROR.ARGUMENT_ERROR, e)
        } else if (e is IllegalMonitorStateException) {
            ex = ResponseThrowable(ERROR.ARGUMENT_ERROR, e)
        } else if (e is java.net.SocketTimeoutException) {
            ex = ResponseThrowable(ERROR.TIMEOUT_ERROR, e)
        } else if (e is ClassCastException) {
            ex = ResponseThrowable(ERROR.RESULT_ERROR, e)
        } else if (e is java.net.UnknownHostException) {
            ex = ResponseThrowable(ERROR.UNKNOWN, e)
        } else {
            ex = if (!e.message.isNullOrEmpty())
                ResponseThrowable("1000", e.message!!, e)
            else ResponseThrowable(ERROR.UNKNOWN, e)
        }
        return ex
    }
}

