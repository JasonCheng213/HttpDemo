package com.winwin.common.http.cache;


import com.winwin.common.http.RxHttp;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 缓存策略
 * Created by Jason on 2017/9/26.
 */

public enum CacheStrategy {

    /**
     * 先缓存，后网络
     */
    CacheAndRemote {
        @Override
        <T> Observable<CacheData<T>> execute(String key,
                                             Observable<CacheData<T>> cache,
                                             Observable<CacheData<T>> remote) {
            cache = cache.onErrorReturnItem(new CacheData<T>(key, true, null));
            return Observable.concat(cache, remote);
        }
    };

    public final <T> Observable<CacheData<T>> execute(final String key, Observable<T> source) {

        Observable<CacheData<T>> cache = RxHttp.getCache().<T>get(key)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<T, CacheData<T>>() {
                    @Override
                    public CacheData<T> apply(@NonNull T object) throws Exception {
                        return new CacheData<>(key, true, object);
                    }
                });

        Observable<CacheData<T>> remote = source
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<T, CacheData<T>>() {
                    @Override
                    public CacheData<T> apply(@NonNull T t) throws Exception {
                        RxHttp.getCache().put(key, t);
                        return new CacheData<>(key, false, t);
                    }
                });

        return execute(key, cache, remote);
    }

    abstract <T> Observable<CacheData<T>> execute(String key,
                                                  Observable<CacheData<T>> cache,
                                                  Observable<CacheData<T>> remote);

}
