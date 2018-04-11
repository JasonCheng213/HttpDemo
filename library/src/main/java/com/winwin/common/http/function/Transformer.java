package com.winwin.common.http.function;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.winwin.common.http.cache.CacheStrategy;
import com.winwin.common.http.cache.CacheData;

import cn.nekocode.rxlifecycle.LifecyclePublisher;
import cn.nekocode.rxlifecycle.RxLifecycle;
import cn.nekocode.rxlifecycle.compact.RxLifecycleCompact;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * Created by Jason on 2017/9/2.
 */

@SuppressWarnings("unused")
public class Transformer {

    public static <T> ObservableTransformer<T, T> switchSchedulers() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> ObservableTransformer<T, CacheData<T>> cache(final String key, final CacheStrategy strategy) {
        return new ObservableTransformer<T, CacheData<T>>() {

            @Override
            public ObservableSource<CacheData<T>> apply(@NonNull Observable<T> upstream) {
                return strategy.execute(key, upstream);
            }
        };
    }

    public static <T> ObservableTransformer<T, T> bindLifecycle(final Activity activity) {
        return bindLifecycle(activity, LifecyclePublisher.ON_DESTROY);
    }

    public static <T> ObservableTransformer<T, T> bindLifecycle(final Activity activity, final @LifecyclePublisher.Event int lifeEvent) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream
                        .takeUntil(RxLifecycle.bind(activity)
                                .asObservable()
                                .skipWhile(new Predicate<Integer>() {
                                    @Override
                                    public boolean test(@NonNull Integer integer) throws Exception {
                                        return integer != lifeEvent;
                                    }
                                }));
            }
        };
    }

    public static <T> ObservableTransformer<T, T> bindLifecycle(final Fragment fragment) {
        return bindLifecycle(fragment, LifecyclePublisher.ON_DESTROY);
    }

    public static <T> ObservableTransformer<T, T> bindLifecycle(final Fragment fragment, final @LifecyclePublisher.Event int lifeEvent) {
        return new ObservableTransformer<T, T>() {

            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream
                        .takeUntil(RxLifecycleCompact.bind(fragment)
                                .asObservable()
                                .skipWhile(new Predicate<Integer>() {
                                    @Override
                                    public boolean test(@NonNull Integer integer) throws Exception {
                                        return integer != lifeEvent;
                                    }
                                }));
            }
        };
    }
}
