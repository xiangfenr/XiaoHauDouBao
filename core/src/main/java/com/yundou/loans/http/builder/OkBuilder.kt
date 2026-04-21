package com.yundou.loans.http.builder


import com.yundou.loans.http.EasyHttp
import com.yundou.loans.http.MoreBaseUrlInterceptor
import com.yundou.loans.utils.Utils
import okhttp3.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

class OkBuilder(var okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()) :
    Builder<OkHttpClient> {

    private val baseUrlParams = LinkedHashMap<String, String>()
    private val baseHeaders = hashMapOf<String, String?>()
    private var cache: Cache? = null
    private var headersInterceptor = HeadersInterceptor()

    companion object {
        private val sharedConnectionPool = ConnectionPool(8, 5, TimeUnit.MINUTES)

        /**
         * 初始化OkhttpClient 默认配置
         */
        fun defaultBuilder(): OkHttpClient.Builder {
            val httpLoggingInterceptor = LogInterceptor()
            httpLoggingInterceptor.level = LogInterceptor.Level.BODY
            val cacheFile =
                File(Utils.getContext().applicationContext?.cacheDir, "cache")
            val cache = Cache(cacheFile, 1024 * 1024 * 50)
            return OkHttpClient.Builder()
                .cache(cache)
                .connectionPool(sharedConnectionPool)
                .followSslRedirects(true)
                .retryOnConnectionFailure(false)
                .connectTimeout(180L, TimeUnit.SECONDS)
                .readTimeout(180L, TimeUnit.SECONDS)
                .writeTimeout(180L, TimeUnit.SECONDS)
        }

        private fun getConnectionSpec(): ConnectionSpec {
            return ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(
                    TlsVersion.TLS_1_2,
                    TlsVersion.TLS_1_0,
                    TlsVersion.TLS_1_1,
                    TlsVersion.TLS_1_3
                )
                .cipherSuites(
                    CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA256,
                    CipherSuite.TLS_RSA_WITH_AES_256_GCM_SHA384,
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256,
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
                    CipherSuite.TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA,
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                    CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA,
                    CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA256,
                    CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA,
                    CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA
                ).build()
        }
    }

    fun init(): OkBuilder {
        val cacheFile =
            File(Utils.getContext().applicationContext?.cacheDir, "cache")
        val cache = Cache(cacheFile, 1024 * 1024 * 50)
        okHttpClientBuilder = OkHttpClient.Builder()
            .cache(cache)
            .connectionPool(sharedConnectionPool)
            .followSslRedirects(true)
            .connectTimeout(180L, TimeUnit.SECONDS)
            .readTimeout(180L, TimeUnit.SECONDS)
            .writeTimeout(180L, TimeUnit.SECONDS)
        return this
    }

    fun setCache(cache: Cache?): OkBuilder {
        this.cache = cache
        if (this.cache != null)
            okHttpClientBuilder.cache(cache)
        return this
    }

    fun addBaseUrlParam(key: String, value: String): OkBuilder {
        baseUrlParams[key] = value
        return this
    }

    fun addBaseUrlParams(params: LinkedHashMap<String, String>): OkBuilder {
        baseUrlParams.putAll(params)
        return this
    }

    fun addBaseHeader(key: String, value: String): OkBuilder {
        baseHeaders[key] = value
        return this
    }

    fun addBaseHeaders(headers: Map<String, String>?): OkBuilder {
        headers?.let { baseHeaders.putAll(it) }
        return this
    }

    fun addCleanBaseHeaders(headers: HashMap<String, String?>): OkBuilder {
        headers.let {
            baseHeaders.clear()
            baseHeaders.putAll(it)
        }
        return this
    }

    fun addHeaderInterceptor(headersInterceptor: HeadersInterceptor): OkBuilder {
        this.headersInterceptor = headersInterceptor
        return this
    }

    fun debug(
        tag: String = EasyHttp::class.java.simpleName,
        logLevel: LogInterceptor.Level = LogInterceptor.Level.BODY,
        logger: LogInterceptor.Logger = LogInterceptor.Logger.DEFAULT
    ): OkBuilder {
        okHttpClientBuilder.addNetworkInterceptor(LogInterceptor(tag, logLevel, logger))
        return this
    }

    fun connectTimeout(connectTimeout: Long): OkBuilder {
        okHttpClientBuilder.connectTimeout(connectTimeout, TimeUnit.SECONDS)
        return this
    }

    fun readTimeout(readTimeout: Long): OkBuilder {
        okHttpClientBuilder.readTimeout(readTimeout, TimeUnit.SECONDS)
        return this
    }

    fun writeTimeout(writeTimeout: Long): OkBuilder {
        okHttpClientBuilder.writeTimeout(writeTimeout, TimeUnit.SECONDS)
        return this
    }

    fun hostnameVerifier(hostnameVerifier: HostnameVerifier): OkBuilder {
        okHttpClientBuilder.hostnameVerifier(hostnameVerifier)
        return this
    }

    fun sslFactory(
        sslSocketFactory: SSLSocketFactory,
        trustManager: X509TrustManager
    ): OkBuilder {
        okHttpClientBuilder.sslSocketFactory(sslSocketFactory, trustManager)
        return this
    }

    fun cookieStore(cookieJar: CookieJar): OkBuilder {
        okHttpClientBuilder.cookieJar(cookieJar)
        return this
    }

    fun addInterceptor(interceptor: Interceptor): OkBuilder {
        okHttpClientBuilder.addInterceptor(interceptor)
        return this
    }

    fun addNetworkInterceptor(interceptor: Interceptor): OkBuilder {
        okHttpClientBuilder.addNetworkInterceptor(interceptor)
        return this
    }

    override fun build(): OkHttpClient {
        val headersInterceptor = HeadersInterceptor()
        headersInterceptor.putHeaders(baseHeaders)
        okHttpClientBuilder.addInterceptor(MoreBaseUrlInterceptor())
        okHttpClientBuilder.addInterceptor(headersInterceptor)
        okHttpClientBuilder.addInterceptor(TokenInterceptor())
        return okHttpClientBuilder.build()
    }
}

