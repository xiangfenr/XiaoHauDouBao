package com.yundou.loans.http

import android.util.Log
import com.yundou.loans.http.builder.DownFileCallback
import com.yundou.loans.http.builder.HttpException
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.lang.ref.SoftReference

/**
 * 下载观察者(监听)
 */
class DownloadObserver(
    private var download: Download
) : DownloadProgressCallback, Observer<Download> {
    private var downloadCallback: SoftReference<DownFileCallback<Download>>
    lateinit var disposable: Disposable

    override fun onSubscribe(d: Disposable) {
        disposable = d
        download.state = Download.State.WAITING
        if (downloadCallback.get() != null) {
            val progress = getProgress(download.currentSize, download.totalSize)
            Log.e("DownloadObserver", "progress:${progress}")
            val currentTime = System.currentTimeMillis()
            if (currentTime - download.lastRefreshTime > 300) {
                if (progress.toInt() != download.progress) {
                    download.progress = progress.toInt()
                    downloadCallback.get()!!.onProgress(download.state, download, progress)
                }
                download.lastRefreshTime = currentTime
            }

        } else {
            downloadCallback = SoftReference(download.callback!!)
        }
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        download.state = Download.State.ERROR
        Log.e("DownloadObserver", "onError${e.message}")
        downloadCallback.get()?.let {
            val progress = getProgress(download.currentSize, download.totalSize)
            download.progress = progress.toInt()
            it.onFailure(HttpException.instance.handle(e))
        }
        disposable.dispose()
    }

    override fun onNext(t: Download) {
        download.state = Download.State.FINISH
    }

    override fun onComplete() {}

    override fun progress(ctSize: Long, totalSize: Long) {
        var currentSize = ctSize
        if (download.totalSize > totalSize) {
            currentSize += download.totalSize - totalSize
        } else {
            download.totalSize = totalSize
        }
        download.currentSize = currentSize
        if (download.currentSize == download.totalSize && download.totalSize != 0L) {
            download.state = Download.State.FINISH
        }
        if (download.state != Download.State.PAUSE) {
            val progress = download.currentSize.toFloat() / download.totalSize.toFloat()

            val currentTime = System.currentTimeMillis()
            if (currentTime - download.lastRefreshTime > 300) {
                if (progress.toInt() != download.progress) {
                    download.progress = progress.toInt()
                    downloadCallback.get()?.onProgress(download.state, download, progress)
                }
                download.lastRefreshTime = currentTime
            }
        }
    }

    companion object {
        fun getProgress(current: Long, total: Long): Float {
            return current.toFloat() / total.toFloat()
        }
    }

    init {
        downloadCallback = SoftReference(download.callback!!)
    }
}

