package com.demo.http;

import java.io.File;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

/**
 * Created by Jason on 2017/8/28.
 */

public interface ApiService {

    String DOMAIN1 = "http://www.baidu.com/";

    @Headers("Header:value")
    @GET("data/sk/101010100.html")
    Observable<WeatherBean> simple(@Query("param") String param);

    @GET("data/sk/101010100.html")
    Observable<String> getString();

    @Headers({"Domain:" + DOMAIN1})
    @GET("data/sk/101010101.html")
    Observable<String> getNewDomain();

    @GET("http://192.168.0.104:3000/test.json")
    Observable<String> getApi1();

    @GET("http://192.168.0.104:3000/test2.json")
    Observable<String> getApi2(@Query("param") String param);

    @GET("http://192.168.0.104:3000/test3.json")
    Observable<String> getApi3(@Query("param") String param);

    @Streaming
    @GET("http://192.168.0.104:3000/rxjava-essentials-cn.pdf")
    Observable<File> download1();
}
