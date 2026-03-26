package com.yundou.loans.http.builder;/*
 *@author jh
 *create at $
 *description:
 */

import com.yundou.loans.base.EventCenter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class TokenInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Response originalResponse = chain.proceed(request);
        ResponseBody responseBody = originalResponse.body();
        if (responseBody == null) return originalResponse;

        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();
        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }
        String bodyString = buffer.clone().readString(charset);
        try {
            JSONObject jsonObject = new JSONObject(bodyString);

            if (jsonObject.has("error_code")) {
                String code = jsonObject.getString("error_code");
                //登录失效
                if (code.equals("401")) {
                    EventCenter.INSTANCE.postNeedLogin();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return originalResponse;
    }
}

