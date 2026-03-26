package com.yundou.loans.http.builder

import android.util.Log
import io.reactivex.disposables.Disposable

/**
 * http 拦截
 */
class McpObserver<T>(var listener: ResponseCallBack<T>) : BaseObserver<Any>() {

    override fun onComplete() {
        Log.e("picc", "onComplete")
        listener.onComplete()
    }

    override fun onSubscribe(d: Disposable) {
        super.onSubscribe(d)
        Log.e("picc", "onSubscribe")
        listener.start()
    }

    override fun onError(e: Throwable) {
        Log.e("picc", "onError")
        listener.onFailure(HttpException().handle(e))
    }

    override fun onNext(t: Any) {
        Log.e("picc", "onNext")
        converAnyJson(t, listener)
    }
}

