package com.yundou.loans.http

import androidx.annotation.Keep
import com.yundou.loans.http.builder.DownFileCallback
import java.io.Serializable

/**
 * 下载实体类
 */
@Keep
class Download : Serializable {
    var id: Long = 0

    //本地存储地址
    var localUrl: String? = null
        get() = if (field == null) "" else field

    //下载地址
    var serverUrl: String? = null
        get() = if (field == null) "" else field

    //文件大小
    var totalSize: Long = 0

    //当前大小
    var currentSize: Long = 0
    var progress: Int = 0
    var filename = ""
    var state = State.NONE //下载状态
    var lastRefreshTime: Long = 0
    //接口service
    var api: ApiService? = null

    //回调接口
    var callback: DownFileCallback<Download>? = null

    constructor()
    constructor(url: String?) {
        serverUrl = url
    }

    constructor(url: String?, callback: DownFileCallback<Download>) {
        serverUrl = url
        this.callback = callback
    }

    fun getCallBack(): DownFileCallback<*>? {
        return callback
    }

    enum class State {
        NONE,  //无状态
        WAITING,  //等待
        LOADING,  //下载中
        PAUSE,  //暂停
        ERROR,  //错误
        FINISH //完成
    }
}

