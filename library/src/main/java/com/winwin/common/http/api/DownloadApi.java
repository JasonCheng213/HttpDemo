package com.winwin.common.http.api;

import java.io.File;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 定义下载接口
 * Created by Jason on 2017/9/10.
 */

public interface DownloadApi {

    @Streaming
    @GET
    Observable<File> download(@Url String url);

}
