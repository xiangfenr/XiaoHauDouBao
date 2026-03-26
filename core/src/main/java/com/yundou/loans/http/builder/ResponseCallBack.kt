package com.yundou.loans.http.builder

/**
 * 回调接口定义
 */
abstract class ResponseCallBack<T> {

    /**
     * 加载数据成功
     */
    abstract fun onSuccess(response: T)

    /**
     * 加载数据失败
     */
    abstract fun onFailure(e: HttpException?)

    open fun start() {}

    open fun onComplete() {}
}

