package com.winwin.common.http.response;

/**
 * 上传下载进度信息
 * Created by Jason on 2017/9/10.
 */

public class ProgressInfo {

    private final long totalContentLength;
    private final long downloadLength;

    public ProgressInfo(long totalContentLength, long downloadLength) {
        this.totalContentLength = totalContentLength;
        this.downloadLength = downloadLength;
    }

    public int getIntProgress() {
        return (int) getFloatProgress();
    }

    @SuppressWarnings("WeakerAccess")
    public float getFloatProgress() {
        if (totalContentLength <= 0) {
            return 0;
        }
        return (int) (downloadLength * 100f / totalContentLength);
    }

    @Override
    public String toString() {
        return " totalContentLength : " +
                totalContentLength +
                " downloadLength : " +
                downloadLength;
    }
}
