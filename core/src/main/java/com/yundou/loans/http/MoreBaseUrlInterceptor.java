package com.yundou.loans.http;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 合作方接口地址路由。
 * 当前版本仅保留魔力一家合作方，地址写死本地，不再支持服务端动态下发。
 */
public class MoreBaseUrlInterceptor implements Interceptor {

    private static final String MOLI_BASE_URL = "https://api-xy.cdjywh.cn";

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        HttpUrl oldUrl = originalRequest.url();
        Request.Builder builder = originalRequest.newBuilder();
        List<String> urlnameList = originalRequest.headers("urlname");
        if (urlnameList.size() > 0) {
            builder.removeHeader("urlname");
            String urlname = urlnameList.get(0);
            HttpUrl baseUrl = null;
            if ("molierbaUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(MOLI_BASE_URL);
            }

            if (baseUrl == null) {
                return chain.proceed(originalRequest);
            }

            HttpUrl.Builder newUrlBuilder = baseUrl.newBuilder();
            for (String pathSegment : oldUrl.encodedPathSegments()) {
                if (!pathSegment.isEmpty()) {
                    newUrlBuilder.addEncodedPathSegment(pathSegment);
                }
            }
            newUrlBuilder.encodedQuery(oldUrl.encodedQuery());

            Request newRequest = builder.url(newUrlBuilder.build()).build();
            return chain.proceed(newRequest);
        } else {
            return chain.proceed(originalRequest);
        }
    }
}
