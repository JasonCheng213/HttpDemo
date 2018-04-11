package com.winwin.common.http.interceptor;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 动态域名拦截器
 * Created by Jason on 2017/9/2.
 */

public class DynamicDomainInterceptor implements Interceptor {

    private static final String DOMAIN_NAME = "Domain";

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        return chain.proceed(processRequest(chain.request()));
    }

    private Request processRequest(Request request) {
        List<String> headers = request.headers(DOMAIN_NAME);
        if (headers == null || headers.isEmpty()) {
            return request;
        } else {
            if (headers.size() == 1) {
                String domain = headers.get(0);
                if (!TextUtils.isEmpty(domain)) {
                    HttpUrl domainUrl = HttpUrl.parse(domain);
                    if (domainUrl != null) {
                        HttpUrl newHttpUrl = request.url().newBuilder()
                                .scheme(domainUrl.scheme())
                                .host(domainUrl.host())
                                .port(domainUrl.port())
                                .build();
                        return request.newBuilder()
                                .removeHeader(DOMAIN_NAME)
                                .url(newHttpUrl)
                                .build();
                    } else {
                        throw new IllegalArgumentException(String.format("域名格式不正确，%s", domain));
                    }
                } else {
                    return request;
                }
            } else {
                throw new IllegalArgumentException("在Headers里最多只能有一个域名");
            }
        }
    }
}
