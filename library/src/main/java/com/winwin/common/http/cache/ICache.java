package com.winwin.common.http.cache;

import io.reactivex.Observable;

/**
 * 定义缓存接口
 * Created by Jason on 2017/9/26.
 */

public interface ICache {

    /**
     * 获取数据
     *
     * @param key 缓存key值
     * @param <T> 缓存对象类型
     * @return Observable<T>
     */
    <T> Observable<T> get(String key);

    /**
     * 存储数据
     *
     * @param key    缓存key值
     * @param object 缓存对象
     * @return true or false
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean put(String key, Object object);

}
