package com.winwin.common.http;

import android.content.Context;
import android.support.v4.util.SimpleArrayMap;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.winwin.common.http.cache.ICache;
import com.winwin.common.http.convert.FileConvertFactory;
import com.winwin.common.http.convert.GsonConverterFactory;
import com.winwin.common.http.interceptor.DownloadInterceptor;
import com.winwin.common.http.interceptor.DynamicDomainInterceptor;
import com.winwin.common.http.interceptor.ParamsInterceptor;
import com.winwin.common.http.log.HttpLoggingInterceptor;
import com.winwin.common.http.log.ILogger;
import com.winwin.common.http.response.DownloadObserver;
import com.winwin.common.http.response.ProgressInfo;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Consumer;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Http
 * Created by Jason on 2017/8/28.
 */

@SuppressWarnings("All")
public class RxHttp {

    private static Context sContext;
    private static Builder sBuilder;
    private static Retrofit sRetrofit;
    private static OkHttpClient sOkHttpClient;
    private static ICache sCache;
    private static boolean sEnableLogger;
    private static ILogger sLogger;

    private RxHttp() {
    }

    public static void setContext(Context context) {
        sContext = context;
    }

    public static Context getContext() {
        return sContext;
    }

    public static void init(Builder builder) {
        sBuilder = builder;
        buildRetrofit(null, null, builder);
    }

    public static boolean isDebug() {
        return sEnableLogger;
    }

    public static ILogger getLogger() {
        return sLogger;
    }

    private static Retrofit buildRetrofit(@Nullable Retrofit retrofit, @Nullable OkHttpClient okHttpClient, @NonNull Builder builder) {
        boolean isCreate = retrofit == null || okHttpClient == null;
        OkHttpClient.Builder okHttpBuilder = isCreate ? new OkHttpClient.Builder() : okHttpClient.newBuilder();
        Retrofit.Builder retrofitBuilder = isCreate ? new Retrofit.Builder() : retrofit.newBuilder();
        okHttpBuilder.readTimeout(builder.readTimeout, TimeUnit.SECONDS)
                .writeTimeout(builder.writerTimeout, TimeUnit.SECONDS)
                .connectTimeout(builder.connectTimeout, TimeUnit.SECONDS);
        if (isCreate) {
            okHttpBuilder
                    .addInterceptor(new HttpLoggingInterceptor())
                    .addInterceptor(new DynamicDomainInterceptor())
                    .addInterceptor(new ParamsInterceptor(builder.headers, builder.params));
            if (builder.cookieJar != null)
                okHttpBuilder.cookieJar(builder.cookieJar);
        } else {
            SimpleArrayMap<String, String> globalHeaders = null;
            SimpleArrayMap<String, Object> globalParams = null;
            for (int i = 0; i < okHttpBuilder.interceptors().size(); i++) {
                if (okHttpBuilder.interceptors().get(i) instanceof ParamsInterceptor) {
                    globalHeaders = ((ParamsInterceptor) okHttpBuilder.interceptors().get(i)).getHeaders();
                    globalParams = ((ParamsInterceptor) okHttpBuilder.interceptors().get(i)).getParams();
                    okHttpBuilder.interceptors().remove(i);
                }
            }
            if (globalHeaders != null && !globalHeaders.isEmpty()) {
                for (int i = 0; i < globalHeaders.size(); i++) {
                    if (!builder.headers.containsKey(globalHeaders.keyAt(i)))
                        builder.headers.put(globalHeaders.keyAt(i), globalHeaders.valueAt(i));
                }
            }
            if (globalParams != null && !globalParams.isEmpty()) {
                for (int i = 0; i < globalParams.size(); i++) {
                    if (!builder.params.containsKey(globalParams.keyAt(i)))
                        builder.params.put(globalParams.keyAt(i), globalParams.valueAt(i));
                }
            }
            okHttpBuilder.addInterceptor(new ParamsInterceptor(builder.headers, builder.params));
            okHttpBuilder.addInterceptor(new DownloadInterceptor(builder.downloadFilePath, builder.progressConsumer));
        }
        OkHttpClient newOkHttpClient = okHttpBuilder.build();

        retrofitBuilder.baseUrl(builder.mainDomain)
                .client(newOkHttpClient);
        if (isCreate) {
            retrofitBuilder.addConverterFactory(FileConvertFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        }
        Retrofit newRetrofit = retrofitBuilder.build();
        if (isCreate) {
            sRetrofit = newRetrofit;
            sOkHttpClient = newOkHttpClient;
            sCache = builder.cache;
            sEnableLogger = builder.enableLogger;
            sLogger = builder.logger;
        }
        return newRetrofit;
    }

    public static Retrofit getRetrofit() {
        return sRetrofit;
    }

    public static OkHttpClient getOkHttpClient() {
        return sOkHttpClient;
    }

    public static ICache getCache() {
        return sCache;
    }

    public static Builder config() {
        return new Builder(sBuilder);
    }

    public static <T> T create(Class<T> service) {
        return getRetrofit().create(service);
    }

    public static void download(String url, boolean useCached, File saveDir, DownloadObserver downloadObserver) {
        download(url, useCached, saveDir, null, downloadObserver);
    }

    public static void download(String url, boolean useCached, File saveDir, String saveName, DownloadObserver downloadObserver) {
        config().download(url, useCached, saveDir, saveName, downloadObserver);
    }

    public static void isDownloaded(String url, File dir, String name) {
        new DownloadUtil().isCached(url, dir, name);
    }

    public static void cancelDownload(String url, File saveDir) {
        cancelDownload(url, saveDir, null);
    }

    public static void cancelDownload(String url, File saveDir, String saveName) {
        new DownloadUtil().cancel(url, saveDir, saveName);
    }

    public static class Builder {

        private int readTimeout = 10;
        private int writerTimeout = 10;
        private int connectTimeout = 10;
        private int retryCount = 0;
        private String mainDomain;
        private final SimpleArrayMap<String, String> headers = new SimpleArrayMap<>();
        private final SimpleArrayMap<String, Object> params = new SimpleArrayMap<>();
        private CookieJar cookieJar;
        private String downloadFilePath;
        private Consumer<ProgressInfo> progressConsumer;
        private ICache cache;
        private boolean enableLogger;
        private ILogger logger;

        public Builder() {
        }

        public Builder(Builder builder) {
            this.readTimeout = builder.readTimeout;
            this.writerTimeout = builder.writerTimeout;
            this.connectTimeout = builder.connectTimeout;
            this.mainDomain = builder.mainDomain;
        }

        public Builder setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder setWriterTimeout(int writerTimeout) {
            this.writerTimeout = writerTimeout;
            return this;
        }

        public Builder setMainDomain(String mainDomain) {
            this.mainDomain = mainDomain;
            return this;
        }

        public Builder addHeader(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public Builder addParam(String key, Object value) {
            params.put(key, value);
            return this;
        }

        public Builder setRetryCount(int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public Builder setCookieJar(CookieJar cookieJar) {
            this.cookieJar = cookieJar;
            return this;
        }

        Builder setProgressConsumer(Consumer<ProgressInfo> progressConsumer) {
            this.progressConsumer = progressConsumer;
            return this;
        }

        Builder setDownloadFilePath(String downloadFilePath) {
            this.downloadFilePath = downloadFilePath;
            return this;
        }

        public Builder setCache(ICache cache) {
            this.cache = cache;
            return this;
        }

        public Builder setEnableLogger(boolean enable) {
            this.enableLogger = enable;
            return this;
        }

        public Builder setLogger(ILogger logger) {
            this.logger = logger;
            return this;
        }

        public <T> T create(Class<T> service) {
            return buildRetrofit(sRetrofit, sOkHttpClient, this).create(service);
        }

        public void download(String url, boolean useCached, File saveDir, String saveName, DownloadObserver downloadObserver) {
            new DownloadUtil().download(this, url, useCached, saveDir, saveName, downloadObserver);
        }
    }

}
