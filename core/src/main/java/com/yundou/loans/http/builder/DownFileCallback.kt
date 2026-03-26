package com.yundou.loans.http.builder

import com.yundou.loans.http.Download

/**
 * 下载方法回调函数
 */
abstract class DownFileCallback<T> : ResponseCallBack<T>() {

    abstract fun onProgress(
        state: Download.State,
        download: Download, progress: Float
    )
}

