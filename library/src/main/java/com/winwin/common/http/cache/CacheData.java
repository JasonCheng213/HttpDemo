package com.winwin.common.http.cache;

/**
 * 请求缓存时返回的数据类型
 * Created by Jason on 2017/9/25.
 */

@SuppressWarnings("ALL")
public class CacheData<T> {

    /**
     * 数据缓存key
     */
    public String key;
    /**
     * 是否是缓存数据
     */
    public boolean isFromCache;
    /**
     * 具体数据类型
     */
    public T data;

    CacheData(String key, boolean isFromCache, T data) {
        this.key = key;
        this.data = data;
        this.isFromCache = isFromCache;
    }

    @Override
    public String toString() {
        return "[" +
                "key:" +
                key +
                ", " +
                "isCache:" +
                isFromCache +
                ", " +
                "data:" +
                data.toString() +
                "]";
    }
}
