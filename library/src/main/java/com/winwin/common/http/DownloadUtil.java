package com.winwin.common.http;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.winwin.common.http.api.DownloadApi;
import com.winwin.common.http.function.Transformer;
import com.winwin.common.http.response.DownloadObserver;
import com.winwin.common.http.response.ErrorMessage;
import com.winwin.common.http.response.ProgressInfo;
import com.winwin.common.http.util.Utils;

import java.io.File;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 下载工具类
 * Created by Jason on 2017/9/10.
 */

public class DownloadUtil {

    public static final ArrayMap<String, Disposable> DOWNLOADING_MAP = new ArrayMap<>();
    private static final int DEFINE_LONG_TIMEOUT = 12 * 60 * 60;//12小时

    void download(RxHttp.Builder builder, String url, boolean useCached, File saveDir, String saveName, final DownloadObserver downloadObserver) {
        File savedFile = getSaveFilePath(url, saveDir, saveName);
        boolean newDownload = !useCached || !isCorrectFile(savedFile);
        if (newDownload) {
            String downloadTag = getTag(url, saveDir, saveName);
            if (DOWNLOADING_MAP.containsKey(downloadTag)) {
                downloadObserver.fail(new ErrorMessage(ErrorMessage.CODE_ERROR_ALREADY_DOWNLOADING, "正在下载中，请勿重复下载."), null);
            } else {
                downloadObserver.setTag(downloadTag);
                builder.setConnectTimeout(DEFINE_LONG_TIMEOUT)
                        .setReadTimeout(DEFINE_LONG_TIMEOUT)
                        .setWriterTimeout(DEFINE_LONG_TIMEOUT)
                        .setDownloadFilePath(savedFile.getAbsolutePath())
                        .setProgressConsumer(new Consumer<ProgressInfo>() {
                            @Override
                            public void accept(@NonNull ProgressInfo progressInfo) throws Exception {
                                downloadObserver.progress(progressInfo);
                            }
                        })
                        .create(DownloadApi.class)
                        .download(url)
                        .compose(Transformer.<File>switchSchedulers())
                        .subscribe(downloadObserver);
            }
        } else {
            downloadObserver.start();
            downloadObserver.progress(new ProgressInfo(savedFile.length(), savedFile.length()));
            downloadObserver.success(savedFile);
            downloadObserver.finish();
        }
    }

    void cancel(String url, File saveDir, String saveName) {
        String tag = getTag(url, saveDir, saveName);
        Disposable disposable = DOWNLOADING_MAP.get(tag);
        if (disposable != null) {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
            File file = getSaveFilePath(url, saveDir, saveName);
            if (isCorrectFile(file))
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            DOWNLOADING_MAP.remove(tag);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    boolean isCached(String url, File dir, String name) {
        return isCorrectFile(getSaveFilePath(url, dir, name));
    }

    private String getTag(String url, File dir, String name) {
        return Utils.md5(url + getSaveFilePath(url, dir, name));
    }

    private boolean isCorrectFile(@NonNull File file) {
        return file.exists() && file.length() > 0;
    }

    private File getSaveFilePath(String url, File dir, String name) {
        if (TextUtils.isEmpty(name)) {
            name = Utils.md5(url) + Utils.getUrlExtension(url);
        }
        return new File(dir, name);
    }

}
