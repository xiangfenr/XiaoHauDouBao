package com.yundou.loans.http;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 通过Interceptor回调监听Response进度
 */
public class DownloadInterceptor implements Interceptor {

    private DownloadProgressCallback callback;

    public DownloadInterceptor(DownloadProgressCallback callback) {
        this.callback = callback;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Log.e("intercept ------------","intercept");
        Response response = chain.proceed(chain.request());

        return response.newBuilder()
                .body(new DownloadResponseBody(response.body(), callback))
                .build();
    }
}

