package com.yundou.loans.base

import android.text.TextUtils
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

class ResponseThrowable : Exception {
    var code: String? = ""
    var errMsg: String? = ""
    var data: String? = ""

    constructor(error: ERROR, e: Throwable? = null) : super(e) {
        code = error.getKey().toString()
        errMsg = error.getValue()
        if (e is SSLException) {
            this.code = ERROR.NETWORD_ERROR.getKey().toString()
        }
        if (e is SocketTimeoutException) {
            this.code = ERROR.NETWORD_ERROR.getKey().toString()
        }
        if (e is UnknownHostException) {
            this.code = ERROR.NETWORD_ERROR.getKey().toString()
        }
        if (e is HttpException) {
            if (e.code() != 500 && e.code() != 400 && e.code() != 410) {
                this.code = ERROR.NETWORD_ERROR.getKey().toString()
            }
        }
    }

    constructor(
        code: String? = "",
        msg: String?,
        e: Throwable? = null,
        data: String? = null,
        traceId: String? = null,
    ) : super(e) {

        this.code = code
        this.data = data
        msg?.let {
            this.errMsg = it
            if (!TextUtils.isEmpty(data)) {
                val urls = data?.split("/")
                urls?.let { burl ->
                    var lastP = burl[burl.size - 1]
                    if (burl[burl.size - 1].indexOf("?") > 0) {
                        var p = lastP.substring(0, lastP.indexOf("?"))
                        if (p.indexOf("}") > 0) {
                            p = p.substring(0, p.indexOf("}"))
                        }
                        this.errMsg =
                            "$code： " + burl[burl.size - 2] + ":$p : $traceId \n" + this.errMsg
                    } else {
                        if (lastP.indexOf("}") > 0) {
                            lastP = lastP.substring(0, lastP.indexOf("}"))
                        }

                        this.errMsg =
                            "$code：" + burl[burl.size - 2] + ":$lastP : $traceId \n" + this.errMsg
                    }
                }

            }
        }
        if (e is SSLException) {
            this.code = ERROR.NETWORD_ERROR.getKey().toString()
        }
        if (e is SocketTimeoutException) {
            this.code = ERROR.NETWORD_ERROR.getKey().toString()
        }
        if (e is UnknownHostException) {
            this.code = ERROR.NETWORD_ERROR.getKey().toString()
        }
        if (e is HttpException) {
            if (e.code() != 500 && e.code() != 400 && e.code() != 410) {
                this.code = ERROR.NETWORD_ERROR.getKey().toString()
            }
        }
    }
}

