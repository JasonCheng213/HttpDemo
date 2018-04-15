package com.demo.http;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUIProgressBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.winwin.common.http.RxHttp;
import com.winwin.common.http.cache.CacheData;
import com.winwin.common.http.cache.CacheStrategy;
import com.winwin.common.http.function.Transformer;
import com.winwin.common.http.response.BaseObserver;
import com.winwin.common.http.response.DownloadObserver;
import com.winwin.common.http.response.ErrorMessage;
import com.winwin.common.http.response.ProgressInfo;
import com.winwin.common.rxcache.RxCache;

import java.io.File;
import java.util.concurrent.TimeUnit;

import cn.nekocode.rxlifecycle.LifecyclePublisher;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;

import static com.winwin.common.http.RxHttp.create;


public class MainActivity extends HttpActivity {

    private static final String TAG = "MainActivity";

    Gson mGson = new GsonBuilder().setPrettyPrinting().create();
    QMUITipDialog mTipDialog;
    QMUIProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在加载")
                .create();
        mTipDialog.setCancelable(true);
        mProgressBar = (QMUIProgressBar) findViewById(R.id.progress_bar);

        Logger.d("on main create");

        RxCache rxCache = new RxCache();
        rxCache.create(CacheApi.class).getUser("123");

        create(ApiService.class)
                .getString()
                .delay(5, TimeUnit.SECONDS)
                .compose(Transformer.<String>bindLifecycle(this, LifecyclePublisher.ON_DESTROY))
                .compose(Transformer.<String>switchSchedulers())
                .compose(Transformer.<String>cache("123", CacheStrategy.CacheAndRemote))
                .subscribe(new DemoObserver<CacheData<String>>(mTipDialog) {

                    @Override
                    public void success(@NonNull CacheData<String> stringCacheData) {
                        super.success(stringCacheData);
                    }
                });
    }

    public void onSimpleClick(View view) {
        simpleRequest();
    }

    public void onStringClick(View view) {
        stringRequest();
    }

    public void onDomainClick(View view) {
        changeDomainRequest();
    }

    public void onZipSynClick(View view) {
        requestZipSyn();
    }

    public void onZipAsyClick(View view) {
        requestZipAsy();
    }

    public void onDownloadClick(View view) {
        download();
    }

    public void onCancelDownloadClick(View view) {
        RxHttp.cancelDownload(mDownloadUrl, mDownloadCacheFile);
    }

    private String mDownloadUrl = "http://imtt.dd.qq.com/16891/CD2BB06784B248708E29DB370160CED2.apk?fsname=com.bench.yylc_4.3.9.0_54316.apk&csr=1bbd";
    private File mDownloadCacheFile = Environment.getExternalStorageDirectory();

    private void download() {
        RxHttp.download(mDownloadUrl, false, mDownloadCacheFile, new DownloadObserver() {
            @Override
            public void start() {
                Log.d(TAG, "start: ");
            }

            @Override
            public void fail(@NonNull ErrorMessage errorMessage, @Nullable File file) {
                Log.d(TAG, "fail: " + errorMessage.getLogMessage());
            }

            @Override
            public void success(@NonNull File file) {
                Log.d(TAG, "success: " + file.getAbsolutePath());
            }

            @Override
            public void finish() {
                Log.d(TAG, "finish: ");
            }

            @Override
            public void progress(ProgressInfo progressInfo) {
                Log.d(TAG, String.format("progress: %s", progressInfo));
                mProgressBar.setProgress(progressInfo.getIntProgress());
            }
        });
    }

    private void requestZipSyn() {
        create(ApiService.class)
                .getApi1()
                .compose(Transformer.<String>switchSchedulers())
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(@NonNull String s) throws Exception {
                        return create(ApiService.class).getApi2("result1:" + s).compose(Transformer.<String>switchSchedulers());
                    }
                })
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(@NonNull String s) throws Exception {
                        return create(ApiService.class).getApi3("result2:" + s).compose(Transformer.<String>switchSchedulers());
                    }
                })
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void start(@NonNull Disposable d) {
                        mTipDialog.show();
                        Log.d(TAG, "start: ");
                    }

                    @Override
                    public void fail(@NonNull ErrorMessage errorMessage, @Nullable String s) {
                        Log.d(TAG, "fail: " + errorMessage.getLogMessage());
                    }

                    @Override
                    public void success(@NonNull String s) {
                        Log.d(TAG, "success: " + s);
                    }

                    @Override
                    public void finish() {
                        Log.d(TAG, "finish: ");
                        mTipDialog.dismiss();
                    }
                });
    }

    private void requestZipAsy() {
        Observable<String> api1 = create(ApiService.class).getApi1().compose(Transformer.<String>switchSchedulers());
        Observable<String> api2 = create(ApiService.class).getApi2("param2").compose(Transformer.<String>switchSchedulers());
        Observable<String> api3 = create(ApiService.class).getApi3("param3").compose(Transformer.<String>switchSchedulers());
        Observable.zip(api1, api2, api3, new Function3<String, String, String, String>() {
            @Override
            public String apply(@NonNull String s, @NonNull String s2, @NonNull String s3) throws Exception {
                return "s1: " + s + " s2: " + s2 + " s3: " + s3;
            }
        })
                .compose(Transformer.<String>switchSchedulers())
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void start(@NonNull Disposable d) {
                        mTipDialog.show();
                        Log.d(TAG, "start: ");
                    }

                    @Override
                    public void fail(@NonNull ErrorMessage errorMessage, @Nullable String s) {
                        Log.d(TAG, "fail: " + errorMessage.getLogMessage());
                    }

                    @Override
                    public void success(@NonNull String s) {
                        Log.d(TAG, "success: " + s);
                    }

                    @Override
                    public void finish() {
                        Log.d(TAG, "finish: ");
                        mTipDialog.dismiss();
                    }
                });
    }

    private void changeDomainRequest() {
        create(ApiService.class)
                .getNewDomain()
                .compose(Transformer.<String>switchSchedulers())
                .subscribe(new DemoObserver<String>(mTipDialog) {
                    @Override
                    public void fail(@NonNull ErrorMessage errorMessage, @Nullable String s) {
                        new QMUIDialog.MessageDialogBuilder(MainActivity.this)
                                .setMessage(errorMessage.getLogMessage())
                                .show();
                    }

                    @Override
                    public void success(@NonNull String s) {
                        new QMUIDialog.MessageDialogBuilder(MainActivity.this)
                                .setMessage(s)
                                .show();
                    }
                });
    }

    private void stringRequest() {
        create(ApiService.class)
                .getString()
                .compose(Transformer.<String>switchSchedulers())
                .subscribe(new DemoObserver<String>(mTipDialog) {
                    @Override
                    public void fail(@NonNull ErrorMessage errorMessage, @Nullable String s) {
                        new QMUIDialog.MessageDialogBuilder(MainActivity.this)
                                .setMessage(errorMessage.getLogMessage())
                                .show();
                    }

                    @Override
                    public void success(@NonNull String s) {
                        new QMUIDialog.MessageDialogBuilder(MainActivity.this)
                                .setMessage(s)
                                .show();
                    }
                });
    }

    private void simpleRequest() {
        create(ApiService.class)
                .simple("param1")
                .compose(Transformer.<WeatherBean>switchSchedulers())
                .subscribe(new DemoObserver<WeatherBean>(mTipDialog) {
                    @Override
                    public void fail(@NonNull ErrorMessage errorMessage, @Nullable WeatherBean weatherBean) {
                        new QMUIDialog.MessageDialogBuilder(MainActivity.this)
                                .setMessage(errorMessage.getLogMessage())
                                .show();
                    }

                    @Override
                    public void success(@NonNull WeatherBean weatherBean) {
                        new QMUIDialog.MessageDialogBuilder(MainActivity.this)
                                .setMessage(mGson.toJson(weatherBean))
                                .show();
                    }
                });
    }

}
