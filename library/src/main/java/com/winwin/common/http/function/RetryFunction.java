package com.winwin.common.http.function;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * 重试功能
 * Created by Jason on 2017/9/9.
 */

@SuppressWarnings("unused")
public class RetryFunction implements Function<Observable<Throwable>, ObservableSource<?>> {

    private static final int DEFINE_RETRY_TIME_DURATION = 1;
    private static int MAX_COUNT = 0;
    private int mCurCount = 0;

    public RetryFunction(int retryMaxCount) {
        MAX_COUNT = retryMaxCount;
    }

    @Override
    public ObservableSource<?> apply(@NonNull Observable<Throwable> throwableObservable) throws Exception {
        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(@NonNull Throwable throwable) throws Exception {
                if (throwable instanceof IOException) {
                    if (mCurCount++ < MAX_COUNT) {
                        return Observable.timer(DEFINE_RETRY_TIME_DURATION, TimeUnit.SECONDS);
                    }
                }
                return Observable.error(throwable);
            }
        });
    }
}
