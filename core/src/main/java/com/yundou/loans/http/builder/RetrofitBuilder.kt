package com.yundou.loans.http.builder


import android.text.TextUtils
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.yundou.loans.base.BaseApp
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitBuilder(val retrofitBuilder: Retrofit.Builder = Retrofit.Builder()) :
    Builder<Retrofit> {
    private var baseUrl: String? = null
    private var isEnableRxJava: Boolean = false

    companion object {
        fun defaultRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BaseApp.Companion.context.baseUrl)
                .client(OkBuilder.defaultBuilder().build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    fun init(): RetrofitBuilder {
        return this
    }

    fun baseUrl(baseUrl: String): RetrofitBuilder {
        this.baseUrl = baseUrl
        if (TextUtils.isEmpty(this.baseUrl)) {
            this.baseUrl = BaseApp.context.baseUrl
        }
        retrofitBuilder.baseUrl(baseUrl)
        return this
    }

    fun client(okHttpClient: OkBuilder?): RetrofitBuilder {
        retrofitBuilder.client(okHttpClient?.build())
        return this
    }

    fun setEnableRxJava(isEnable: Boolean): RetrofitBuilder {
        isEnableRxJava = isEnable
        return this
    }

    override fun build(): Retrofit {
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create())
        if (isEnableRxJava)
            retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        else {
            retrofitBuilder.addCallAdapterFactory(CoroutineCallAdapterFactory())
        }
        return retrofitBuilder.build()
    }
}

