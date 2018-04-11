package com.winwin.common.http.interceptor;

import android.support.annotation.NonNull;
import android.support.v4.util.SimpleArrayMap;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 动态参数添加拦截器
 * Created by Jason on 2017/9/6.
 */

public class ParamsInterceptor implements Interceptor {

    private final SimpleArrayMap<String, String> headers;
    private final SimpleArrayMap<String, Object> params;

    public ParamsInterceptor(SimpleArrayMap<String, String> headers, SimpleArrayMap<String, Object> params) {
        this.headers = headers;
        this.params = params;
    }

    public SimpleArrayMap<String, String> getHeaders() {
        return headers;
    }

    public SimpleArrayMap<String, Object> getParams() {
        return params;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder newRequestBuilder = null;
        if (headers != null && !headers.isEmpty()) {
            newRequestBuilder = original.newBuilder();
            for (int i = 0; i < headers.size(); i++) {
                newRequestBuilder.header(headers.keyAt(i), headers.valueAt(i));
            }
        }
        if (params != null && !params.isEmpty()) {
            if (newRequestBuilder == null)
                newRequestBuilder = original.newBuilder();

            if ("GET".equals(original.method())) {
                HttpUrl.Builder newUrlBuilder = original.url().newBuilder();
                for (int i = 0; i < params.size(); i++) {
                    newUrlBuilder.addQueryParameter(params.keyAt(i), String.valueOf(params.valueAt(i)));
                }
                newRequestBuilder.url(newUrlBuilder.build());
            } else if ("POST".equals(original.method())) {
                if (original.body() instanceof FormBody) {
                    FormBody.Builder newBody = new FormBody.Builder();
                    for (int i = 0; i < params.size(); i++) {
                        newBody.add(params.keyAt(i), String.valueOf(params.valueAt(i)));
                    }
                    newRequestBuilder.post(newBody.build());
                } else {
                    throw new RuntimeException("not support this body type.");
                }
            }
        }
        return newRequestBuilder == null ? chain.proceed(original) : chain.proceed(newRequestBuilder.build());
    }

}
