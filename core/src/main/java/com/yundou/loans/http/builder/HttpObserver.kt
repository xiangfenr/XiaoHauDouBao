package com.yundou.loans.http.builder

import com.yundou.loans.http.ResultBean
import com.yundou.loans.utils.LogUtils
import io.reactivex.disposables.Disposable

class HttpObserver<T>(var listener: ResponseCallBack<T>) : BaseObserver<ResultBean<Any>>() {

    override fun onComplete() {}

    override fun onSubscribe(d: Disposable) {
        super.onSubscribe(d)
    }

    override fun onError(e: Throwable) {
        listener.onFailure(HttpException().handle(e))
    }

    override fun onNext(t: ResultBean<Any>) {
        LogUtils.d("zxzzxaxasd")
        converJson(t, listener)
    }
}

