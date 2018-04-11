package com.winwin.common.http.response;

import com.google.gson.JsonParseException;
import com.winwin.common.http.RxHttp;
import com.winwin.common.http.cache.CacheData;
import com.winwin.common.http.util.Utils;

import java.io.File;
import java.net.ConnectException;
import java.net.UnknownHostException;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * 定义请求结果回调基类
 * Created by Jason on 2017/9/2.
 */

public abstract class BaseObserver<T> implements Observer<T> {

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        start(d);
    }

    @Override
    public void onNext(@NonNull T t) {
        //文件格式
        if (t instanceof File) {
            success(t);
            return;
        }
        //分析数据
        Object realData = t;
        if (t instanceof CacheData) {
            realData = ((CacheData) t).data;
        }
        if (realData instanceof String) {
            success(t);
        } else if (realData instanceof IData) {
            int businessCode = ((IData) realData).getBusinessCode();
            if (businessCode == ErrorMessage.CODE_SUCCESS) {
                success(t);
            } else {
                fail(new ErrorMessage(businessCode), t);
            }
        } else {
            throw new RuntimeException("who extends BaseObserver<T>, T must implement IData interface.");
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
//        if (true)
//            e.printStackTrace();
        // TODO: 2017/9/4
        if (e instanceof UnknownHostException) {
            if (Utils.isNetworkConnected(RxHttp.getContext())) {
                fail(new ErrorMessage(ErrorMessage.CODE_ERROR_UNKNOWN_HOST, e), null);
            } else {
                fail(new ErrorMessage(ErrorMessage.CODE_ERROR_NO_CONNECTION, e), null);
            }
        } else if (e instanceof HttpException) {
            fail(new ErrorMessage(ErrorMessage.CODE_ERROR_OTHER, e), null);
        } else if (e instanceof ConnectException) {
            fail(new ErrorMessage(ErrorMessage.CODE_ERROR_OTHER, e), null);
        } else if (e instanceof JsonParseException) {
            fail(new ErrorMessage(ErrorMessage.CODE_ERROR_OTHER, e), null);
        } else {
            fail(new ErrorMessage(ErrorMessage.CODE_ERROR_OTHER, e), null);
        }
        finish();
    }

    @Override
    public void onComplete() {
        finish();
    }

    public abstract void start(@SuppressWarnings("UnusedParameters") @NonNull Disposable d);

    public abstract void fail(@NonNull ErrorMessage errorMessage, @Nullable T t);

    public abstract void success(@NonNull T t);

    public abstract void finish();

}
