package com.yundou.loans.http.builder

import com.google.gson.Gson
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.lang.Exception
import java.lang.reflect.ParameterizedType

abstract class BaseObserver<T> : Observer<T> {
    var disposable: Disposable? = null

    override fun onComplete() {}

    override fun onSubscribe(d: Disposable) {
        disposable = d
    }

    override fun onNext(t: T & Any) {}

    override fun onError(e: Throwable) {}

    fun dispose() {
        if (disposable != null && !disposable!!.isDisposed) {
            disposable!!.dispose()
        }
    }

    /**
     * 转换返回对象 根据泛型类型处理转换
     */
    fun <T> converJson(response: com.yundou.loans.http.ResultBean<Any>, listener: ResponseCallBack<T>?) {
        var myJson = ""
        if (listener != null) {
            try {
                myJson = if (null != response.result) {
                    Gson().toJson(response.result)
                } else {
                    Gson().toJson(response.data)
                }
                if (listener.javaClass.genericInterfaces.isNotEmpty() && listener.javaClass.genericInterfaces.size > 0) {
                    val type = listener.javaClass.genericInterfaces[0]
                    val typeArgument = (type as ParameterizedType).actualTypeArguments[0]
                    listener.onSuccess(Gson().fromJson<Any>(myJson, typeArgument) as T)

                } else {
                    listener.onSuccess(myJson as T)
                }
            } catch (e: Exception) {
                listener.onFailure(HttpException().handle(e))
            }
        }
    }

    fun <T> converAnyJson(response: Any, listener: ResponseCallBack<T>?) {
        var myJson = ""
        if (listener != null) {
            try {
                myJson = Gson().toJson(response)
                if (listener.javaClass.genericInterfaces.isNotEmpty() && listener.javaClass.genericInterfaces.size > 0) {
                    val type = listener.javaClass.genericInterfaces[0]
                    val typeArgument = (type as ParameterizedType).actualTypeArguments[0]
                    listener.onSuccess(Gson().fromJson<Any>(myJson, typeArgument) as T)

                } else {
                    listener.onSuccess(myJson as T)
                }
            } catch (e: Exception) {
                listener.onFailure(HttpException().handle(e))
            }
        }
    }
}

