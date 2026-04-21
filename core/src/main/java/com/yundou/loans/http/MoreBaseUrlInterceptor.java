package com.yundou.loans.http;

import androidx.annotation.NonNull;

import com.yundou.loans.base.BaseApp;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class MoreBaseUrlInterceptor implements Interceptor {
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
            if ("weimiaoyongUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getWeimiaoyongUrl());
            } else if ("zhixiangdaiUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getZhixiangdaiUrl());
            } else if ("kuaiyidaiUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getKuaiyidaiUrl());
            } else if ("molierbaUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getMolierbaUrl());
            } else if ("twoHeRuiUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getTwoHeRuiUrl());
            } else if ("jiLoanUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getJiLoanUrl());
            } else if ("wqbOrangeUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getWqbOrangeUrl());
            } else if ("jiYongBaseUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getJiYongBaseUrl());
            } else if ("jiYongBangUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getJiYongBangUrl());
            } else if ("yqqbBaseUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getYqqbBaseUrl());
            } else if ("tianxiaFenQiBaseUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getTianxiaFenQiBaseUrl());
            } else if ("zxdNewBaseUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getZxdNewBaseUrl());
            } else if ("yuanXiaoHuaBaseUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getYuanXiaoHuaBaseUrl());
            } else if ("qiDaiBaseUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getQiDaiBaseUrl());
            } else if ("weiRongBaoUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getWeiRongBaoUrl());
            } else if ("jiDaiBaseUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getJiDaiBaseUrl());
            } else if ("xiaoFuBaseUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getXiaoFuBaseUrl());
            } else if ("longYanUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getLongYanUrl());
            } else if ("weiYinBaseUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getWeiYinBaseUrl());
            } else if ("jiYiHuaBaseUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getJiYiHuaBaseUrl());
            } else if ("yueXiangBaseUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getYueXiangBaseUrl());
            } else if ("baJieBaseUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getBaJieBaseUrl());
            } else if ("shanDaiMiaoBaseUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getShanDaiMiaoBaseUrl());
            } else if ("jiYongQianBaoBaseUrl".equals(urlname)) {
                baseUrl = HttpUrl.parse(BaseApp.Companion.getContext().getJiYongQianBaoBaseUrl());
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

