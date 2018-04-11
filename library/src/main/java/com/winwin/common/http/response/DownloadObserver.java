package com.winwin.common.http.response;

import com.winwin.common.http.DownloadUtil;

import java.io.File;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * 下载结果回调
 * Created by Jason on 2017/9/10.
 */

public abstract class DownloadObserver extends BaseObserver<File> implements OnProgressListener {

    private String mTag;

    public void setTag(String tag) {
        mTag = tag;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        DownloadUtil.DOWNLOADING_MAP.put(mTag, d);
        start();
    }

    @Override
    public void onError(@NonNull Throwable e) {
        super.onError(e);
        DownloadUtil.DOWNLOADING_MAP.remove(mTag);
    }

    @Override
    public void onComplete() {
        super.onComplete();
        DownloadUtil.DOWNLOADING_MAP.remove(mTag);
    }

    @Override
    public void start(@NonNull Disposable d) {
    }

    public abstract void start();
}
