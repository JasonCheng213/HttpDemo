package com.winwin.common.http.response;

import android.text.TextUtils;

/**
 * 错误消息
 * Created by Jason on 2017/9/3.
 */

@SuppressWarnings("unused")
public class ErrorMessage {

    public static final int CODE_SUCCESS = 0x10000;
    public static final int CODE_ERROR_NO_CONNECTION = -0x10001;
    public static final int CODE_ERROR_UNKNOWN_HOST = -0x10002;
    public static final int CODE_ERROR_ALREADY_DOWNLOADING = -0x10021;
    public static final int CODE_ERROR_OTHER = -0x10100;

    public int code;
    public String message;
    public Object arg;

    public ErrorMessage(int code) {
        this.code = code;
    }

    public ErrorMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorMessage(int code, Object arg) {
        this.code = code;
        this.arg = arg;
    }

    public ErrorMessage(int code, String message, Object arg) {
        this.code = code;
        this.message = message;
        this.arg = arg;
    }

    public String getLogMessage() {
        String result = message;
        if (TextUtils.isEmpty(result) && arg != null) {
            if (arg instanceof Exception) {
                result = ((Exception) arg).getMessage();
                if (TextUtils.isEmpty(result)) {
                    result = arg.toString();
                }
            }
        }
        return result;
    }
}
