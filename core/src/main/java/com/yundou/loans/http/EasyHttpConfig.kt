package com.yundou.loans.http

import android.app.Application
import android.text.TextUtils
import com.yundou.loans.http.builder.DownloadCache
import com.yundou.loans.http.builder.LogInterceptor
import com.yundou.loans.http.builder.OkBuilder
import com.yundou.loans.http.builder.RetrofitBuilder
import com.yundou.loans.utils.Utils
import okhttp3.Cache
import okhttp3.Interceptor
import retrofit2.Retrofit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

class EasyHttpConfig {

    //初始化默认的OkhttpClient
    var okBuilder: OkBuilder? = null
    var retrofitBuilder: RetrofitBuilder? = null
    var dcache: DownloadCache? = null
    private var retrofit: Retrofit? = null
    private var baseUrl: String = ""
    var headers: Map<String, String>? = null
    private val interceptors: MutableList<Interceptor> = mutableListOf()
    private val networkInterceptors: MutableList<Interceptor> = mutableListOf()
    private var sslSocketFactoryOrNull: SSLSocketFactory? = null
    private var trustManager: X509TrustManager? = null
    private var hostnameVerifier: HostnameVerifier? = null
    private var tag: String? = null
    private var cache: Cache? = null
    private var application: Application? = null

    companion object {
        private var instance: EasyHttpConfig? = null
            get() {
                if (field == null) {
                    field = EasyHttpConfig()
                }
                return field
            }

        fun get(): EasyHttpConfig {
            return instance!!
        }
    }

    fun setRetrofit(retrofit: Retrofit) = apply {
        this.retrofit = retrofit
        return this
    }

    fun setApplication(application: Application) = apply {
        this.application = application
        Utils.init(application)
        return this
    }

    /**
     * 设置自定义Okhttpbuilder
     * 注：如不设置则执行默认设置
     */
    fun setOkBuilder(okBuilder: OkBuilder) = apply {
        this.okBuilder = okBuilder
        return this
    }

    /**
     * 设置自定义retrofitBuilder
     * 注：如不设置则执行默认设置
     */
    fun setRetrofitBuilder(retrofitBuilder: RetrofitBuilder) = apply {
        this.retrofitBuilder = retrofitBuilder
        if (null == okBuilder) {
            okBuilder = OkBuilder().init()
        }
        return this
    }

    fun setBaseUrl(url: String) = apply {
        if (TextUtils.isEmpty(url))
            return this
        baseUrl = url
        return this
    }

    fun addDownLoadDB(dace: DownloadCache) = apply {
        this.dcache = dace
        return this
    }

    fun setCache(cache: Cache) = apply {
        this.cache = cache
        return this
    }

    fun addHeaders(headers: Map<String, String>) = apply {
        this.headers = headers
        return this
    }

    fun debug(tag: String) = apply {
        this.tag = tag
        return this
    }

    /**
     * 添加拦截器
     */
    fun addInterceptor(interceptor: Interceptor) = apply {
        interceptors += interceptor
        return this
    }

    /**
     * 添加网络拦截器
     */
    fun addNetInterceptor(interceptor: Interceptor) = apply {
        networkInterceptors += interceptor
        return this
    }

    /**
     * 添加https 票据验证
     */
    fun setSSlFactory(
        sslSocketFactory: SSLSocketFactory,
        trustManager: X509TrustManager,
    ) = apply {
        this.sslSocketFactoryOrNull = sslSocketFactory
        this.trustManager = trustManager
        return this
    }

    fun setHostnameVerifier(hostnameVerifier: HostnameVerifier) = apply {
        this.hostnameVerifier = hostnameVerifier
        return this
    }

    /**
     * 初始化配置
     * 注：自定义设置需要在init 之前
     */
    fun init() {
        if (application == null) {
            Throwable("init application is null")
            return
        }
        if (application != null) {
            Utils.init(application!!)
        }
        if (okBuilder == null)
            okBuilder = OkBuilder().init()
        okBuilder!!.setCache(cache)
        okBuilder!!.addBaseHeaders(headers)

        for (interceptor in networkInterceptors) {
            okBuilder!!.addNetworkInterceptor(interceptor)
        }
        for (interceptor in interceptors) {
            okBuilder!!.addInterceptor(interceptor)
        }
        if (sslSocketFactoryOrNull != null && trustManager != null) {
            okBuilder!!.sslFactory(sslSocketFactoryOrNull!!, trustManager!!)
        }
        hostnameVerifier?.let {
            okBuilder?.hostnameVerifier(it)
        }

        if (!TextUtils.isEmpty(tag)) {
            okBuilder!!.debug(this.tag.toString(), LogInterceptor.Level.BODY)
        }

        if (retrofitBuilder == null)
            retrofitBuilder = RetrofitBuilder().init()
        retrofitBuilder!!.baseUrl(baseUrl).client(okBuilder!!)
    }
}

