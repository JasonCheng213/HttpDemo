package com.demo.http;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.winwin.common.http.RxHttp;
import com.winwin.common.http.cache.ICache;
import com.winwin.common.http.log.ILogger;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by Jason on 2017/9/6.
 */

public class BaseApplication extends Application implements CookieJar, ILogger {

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.t("httpdemo");
        Logger.addLogAdapter(new AndroidLogAdapter());

        ICache cache = new ICache() {
            @Override
            public <T> Observable<T> get(String key) {
                return Observable.just((T) "123");
            }

            @Override
            public boolean put(String key, Object object) {
                return false;
            }
        };

        RxHttp.init(new RxHttp.Builder()
                .setMainDomain("http://www.weather.com.cn/")
                .setConnectTimeout(10)
                .setCookieJar(this)
                .setCache(cache)
                .setEnableLogger(true)
                .setLogger(this)
                .addHeader("header1", "value1")
                .addHeader("header2", "value2"));
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> list = new ArrayList<>();
        return list;
    }

    @Override
    public void d(String message, Object... args) {
        Logger.d(message, args);
    }

    @Override
    public void e(String message, Object... args) {
        Logger.e(message, args);
    }
}
