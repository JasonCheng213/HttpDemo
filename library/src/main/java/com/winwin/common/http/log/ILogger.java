package com.winwin.common.http.log;

/**
 * 日志接口
 * Created by Jason on 2017/10/3.
 */

public interface ILogger {

    void d(String message, Object... args);

    void e(String message, Object... args);
}
