package com.yundou.loans.http

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
import androidx.annotation.NonNull
import com.yundou.loans.base.BaseApp
import com.yundou.loans.http.builder.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.nio.channels.FileChannel
import java.util.concurrent.TimeUnit

open class EasyHttp {

    private var retrofit: Retrofit? = null
    private var service: ApiService? = null
    private var config: EasyHttpConfig? = null
    private val downloadObservers: java.util.HashMap<String, DownloadObserver> = HashMap()

    /*请求集合对应回调map*/
    private val httpMap: java.util.HashMap<String, BaseObserver<*>> = HashMap()

    /*下载集合*/
    val downloadSet: java.util.HashMap<String, Download> = HashMap()
    private var dCache: DownloadCache? = null

    companion object {
        fun getInstance() = SingleHolder.instance
    }

    /**
     * 通过内部静态类保证线程双重校验的安全性
     **/
    object SingleHolder {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            EasyHttp()
        }
    }

    /**
     *初始化配置
     */
    fun initConfig(@NonNull config: EasyHttpConfig?) {
        if (config != null) {
            getInstance().config = config
        }
        if (retrofit == null) {
            retrofit = getInstance().config?.retrofitBuilder!!.build()
        }
        if (dCache == null) {
            dCache = getInstance().config?.dcache
        }
        service = retrofit!!.create(ApiService::class.java)
    }

    fun getDownloadObservers(): java.util.HashMap<String, BaseObserver<*>> {
        return httpMap
    }

    fun getDownMap(): java.util.HashMap<String, Download> {
        return downloadSet
    }

    /**
     * 使用自定义的api
     */
    fun <C> retrofitCreate(clazz: Class<C>): C {
        if (retrofit == null) {
            Throwable("Please initialize the EasyHttp component")
        }

        return retrofit?.create(clazz) as C
    }

    /**
     * 公用的get 请求
     */
    @SuppressLint("CheckResult")
    operator fun <T> get(url: String, maps: Map<String, Any>, listener: ResponseCallBack<T>) {
        val map: Map<*, *> = maps
        val observer = HttpObserver(listener)
        httpMap[url] = observer
        retrofitCreate(ApiService::class.java).get(url, map as Map<String, Any>)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    /**
     * 公用的get 无参请求
     */
    @SuppressLint("CheckResult")
    operator fun <T> get(url: String, listener: ResponseCallBack<T>) {
        val observer = HttpObserver(listener)
        httpMap[url] = observer
        retrofitCreate(ApiService::class.java).get(url).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(observer)
    }

    /**
     * 公用的get 请求 自定义返回对象
     */
    @SuppressLint("CheckResult")
    fun <T> getAny(url: String, maps: Map<String, Any>, listener: ResponseCallBack<T>) {
        val map: Map<*, *> = maps
        val observer = McpObserver(listener)
        httpMap[url] = observer
        retrofitCreate(ApiService::class.java).getAny(url, map as Map<String, Any>)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    /**
     * 公用的get 无参 自定义返回对象请求
     */
    @SuppressLint("CheckResult")
    fun <T> getAny(url: String, listener: ResponseCallBack<T>) {
        val observer = McpObserver(listener)
        httpMap[url] = observer
        retrofitCreate(ApiService::class.java).getAny(url).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(observer)
    }

    /**
     * 公用的post 请求
     */
    @SuppressLint("CheckResult")
    fun <T> post(url: String, maps: Map<String, Any>, listener: ResponseCallBack<T>) {
        val map: Map<*, *>? = maps
        val observer = HttpObserver(listener)
        httpMap[url] = observer
        service!!.post(url, map as Map<String, String>).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(observer)
    }

    @SuppressLint("CheckResult")
    fun <T> post(url: String, listener: ResponseCallBack<T>) {
        val observer: HttpObserver<T> = HttpObserver(listener)
        httpMap[url] = observer
        service!!.post(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    /**
     * 公用的post 请求（自定义返回对象）
     */
    @SuppressLint("CheckResult")
    fun <T> postAny(url: String, maps: HashMap<String, String>, listener: ResponseCallBack<T>) {
        val map: Map<*, *>? = maps
        val observer = McpObserver(listener)
        httpMap[url] = observer
        service!!.postAny(url, map as Map<String, String>).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(observer)
    }

    @SuppressLint("CheckResult")
    fun <T> postAny(url: String, listener: ResponseCallBack<T>) {
        val observer = McpObserver(listener)
        httpMap[url] = observer
        service!!.postAny(url).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(observer)
    }

    /**
     * 下载单文件，该方法不支持断点下载
     */
    @SuppressLint("CheckResult")
    fun downloadFile(
        url: String, filePath: String?, listener: DownFileCallback<Download>
    ) {
        val download: Download =
            if (downloadSet[url] == null) {
                Download().apply {
                    callback = listener
                    state = Download.State.NONE
                    localUrl = filePath
                    serverUrl = url
                }
            } else {
                downloadSet[url]!!
            }

        dCache?.insertOrUpdate(download)
        val downloadObservable = DownloadObserver(download)
        downloadObservers[url] = downloadObservable
        var progress: Int
        var currentTime: Long

        val downLoadRetrofit = getDownLoadRetrofit { currentSize, totalSize ->
            download.currentSize = currentSize
            download.totalSize = totalSize
            Log.e("downloadFile", "getDownLoadRetrofit ......")
            progress = getProgress(currentSize, totalSize).toInt()
            currentTime = System.currentTimeMillis()
            if (progress != download.progress) {
                download.progress = progress
                download.callback?.onProgress(
                    Download.State.LOADING,
                    download,
                    getProgress(currentSize, totalSize)
                )
            }
            download.lastRefreshTime = currentTime
            Log.e("downloadFile", "getDownLoadRetrofit ......${download.progress}")
            dCache?.insertOrUpdate(download)
        }
        downloadSet[url] = download
        downLoadRetrofit?.create(ApiService::class.java)?.downloadFiles(url)?.subscribeOn(Schedulers.io())
            ?.map {
                createFile(it, download.localUrl, download)
                download
            }?.observeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(downloadObservable)
    }

    private fun getDownLoadRetrofit(listener: DownloadProgressCallback): Retrofit? {
        val okHttpClient: OkHttpClient.Builder = defaultBuilder()
        val time = System.currentTimeMillis().toString()
        val map = LinkedHashMap<String, String?>()
        val head = getInstance().config?.headers

        map["Accept-Encoding"] = "identity"
        map["timestamp"] = time
        head?.let { map.putAll(it) }
        val headersInterceptor = HeadersInterceptor()
        headersInterceptor.putHeaders(map)
        okHttpClient.addInterceptor(headersInterceptor)
        okHttpClient.addInterceptor(TokenInterceptor())
            .addInterceptor(HttpLoggingInterceptor())
            .addInterceptor(MoreBaseUrlInterceptor())
            .addNetworkInterceptor(DownloadInterceptor(listener))
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl(BaseApp.context.baseUrl)
            .client(okHttpClient.build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())

        return retrofitBuilder.build()
    }

    private fun getProgress(current: Long, total: Long): Float {
        return (current.toFloat() / total.toFloat()) * 100
    }

    @Throws(IOException::class)
    private fun createFile(responseBody: ResponseBody, path: String?, download: Download) {
        if (TextUtils.isEmpty(path)) return
        var isError = false
        var randomAccessFile: RandomAccessFile? = null
        var inputStream: InputStream? = null
        var channelOut: FileChannel? = null
        var bufferedInputStream: BufferedInputStream? = null
        try {
            val file = File(path)
            file.createNewFile()
            randomAccessFile = RandomAccessFile(file, "rwd")
            inputStream = responseBody.byteStream()
            val bufferSize = 1024 * 8
            val buffer = ByteArray(bufferSize)
            bufferedInputStream = BufferedInputStream(inputStream, bufferSize)
            var readLength: Int
            val length: Long
            if (responseBody.contentLength() > 0) {
                length = responseBody.contentLength()
                channelOut = randomAccessFile.channel
                val mappedBuffer = channelOut.map(FileChannel.MapMode.READ_WRITE, 0, length)
                while (bufferedInputStream.read(buffer, 0, bufferSize)
                        .also { readLength = it } != -1
                ) {
                    mappedBuffer.put(buffer, 0, readLength)
                    download.currentSize += readLength
                }
            } else {
                val out = FileOutputStream(path)
                while (bufferedInputStream.read(buffer, 0, bufferSize).also {
                        readLength = it
                    } != -1
                ) {
                    out.write(buffer, 0, readLength)
                    download.currentSize += readLength
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isError = true
        } finally {
            inputStream?.close()
            randomAccessFile?.close()
            bufferedInputStream?.close()
            if (!isError) {
                download.progress = 100
                download.callback?.onSuccess(download)
            }
        }
    }

    /**
     * 停止网络请求
     */
    fun stopExecute(url: String) {
        if (TextUtils.isEmpty(url)) return
        if (httpMap.containsKey(url)) {
            httpMap[url]?.dispose()
            httpMap.remove(url)
        }
    }

    fun getDownloadQue(): List<Download> {
        var list: ArrayList<Download>? = null
        if (dCache == null) {
            return list!!
        }
        return dCache!!.queryAll()!!
    }

    fun defaultBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .followSslRedirects(true)
            .connectTimeout(180L, TimeUnit.SECONDS)
            .readTimeout(180L, TimeUnit.SECONDS)
            .writeTimeout(180L, TimeUnit.SECONDS)
    }
}

