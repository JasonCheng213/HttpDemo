package com.winwin.common.http.interceptor;


import android.support.annotation.NonNull;

import com.winwin.common.http.response.ProgressInfo;
import com.winwin.common.http.response.ProgressResponseBody;

import java.io.IOException;

import io.reactivex.functions.Consumer;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 下载拦截器
 * Created by Jason on 2017/9/10.
 */

public class DownloadInterceptor implements Interceptor {

    private final String path;
    private final Consumer<ProgressInfo> progressConsumer;

    public DownloadInterceptor(String path, Consumer<ProgressInfo> progressConsumer) {
        this.path = path;
        this.progressConsumer = progressConsumer;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        if (path != null && progressConsumer != null) {
            Response originalResponse = chain.proceed(request);
            ProgressResponseBody body = new ProgressResponseBody(originalResponse.body(), path, progressConsumer);
            return originalResponse.newBuilder()
                    .body(body)
                    .build();
        }
        return chain.proceed(request);
    }

}
