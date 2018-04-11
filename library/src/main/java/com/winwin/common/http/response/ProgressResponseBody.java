package com.winwin.common.http.response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 自定义ResponseBody，实现进度监听
 * Created by Jason on 2017/9/10.
 */

public class ProgressResponseBody extends ResponseBody {

    private final ResponseBody mResponseBody;
    private final String mFilePath;
    private BufferedSource bufferedSource;
    private ObservableEmitter<ProgressInfo> mEmitter;

    public ProgressResponseBody(ResponseBody responseBody, String filePath, Consumer<ProgressInfo> progressConsumer) {
        mResponseBody = responseBody;
        mFilePath = filePath;
        if (progressConsumer != null) {
            Observable.create(new ObservableOnSubscribe<ProgressInfo>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<ProgressInfo> e) throws Exception {
                    mEmitter = e;
                }
            })
                    .throttleWithTimeout(10, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(progressConsumer);
        }
    }

    public String getFilePath() {
        return mFilePath;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {

            long totalDownloadLength = 0L;

            @Override
            public long read(@NonNull Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalDownloadLength += bytesRead != -1 ? bytesRead : 0;
                if (mEmitter != null) {
                    if (bytesRead == -1) {
                        mEmitter.onComplete();
                    } else {
                        mEmitter.onNext(new ProgressInfo(mResponseBody.contentLength(), totalDownloadLength));
                    }
                }
                return bytesRead;
            }
        };
    }
}
