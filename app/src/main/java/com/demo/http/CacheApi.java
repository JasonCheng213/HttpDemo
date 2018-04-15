package com.demo.http;

import com.winwin.common.rxcache.annotation.Read;

import io.reactivex.Observable;
import retrofit2.http.Query;

/**
 * Created by Jason on 2018/4/15.
 */

public interface CacheApi {

    @Read(key = "key1", type = String.class)
    Observable<WeatherBean> getUser(@Query("param") String param);

}
