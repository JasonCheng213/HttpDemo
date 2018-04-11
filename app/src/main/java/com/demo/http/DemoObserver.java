package com.demo.http;

import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.winwin.common.http.response.BaseObserver;
import com.winwin.common.http.response.ErrorMessage;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Jason on 2017/9/12.
 */

public abstract class DemoObserver<T> extends BaseObserver<T> {

    protected QMUITipDialog mLoading;

    public DemoObserver(QMUITipDialog loading) {
        mLoading = loading;
    }

    @Override
    public void start(@NonNull Disposable d) {
        Logger.d("start: ");
        mLoading.show();
    }

    @Override
    public void fail(@NonNull ErrorMessage errorMessage, @Nullable T t) {
        Logger.d("fail: " + errorMessage.getLogMessage());
    }

    @Override
    public void success(@NonNull T t) {
        Logger.d("success: " + t);
    }

    @Override
    public void finish() {
        Logger.d("finish: ");
        mLoading.dismiss();
    }
}
