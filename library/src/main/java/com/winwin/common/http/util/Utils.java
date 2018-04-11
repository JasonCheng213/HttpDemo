package com.winwin.common.http.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import okhttp3.HttpUrl;

/**
 * 工具类
 * Created by Jason on 2017/9/4.
 */
@SuppressWarnings("All")
public class Utils {

    /**
     * 获取url后缀名
     *
     * @param url resource url
     * @return "" or ".apk"
     */
    @NonNull
    public static String getUrlExtension(@NonNull String url) {
        String fileName = getUrlFileName(url);
        if (fileName.contains(".")) {
            int index = fileName.lastIndexOf(".");
            return fileName.substring(index);
        } else {
            return "";
        }
    }

    @NonNull
    public static String getUrlFileName(@NonNull String url) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) return "";
        List<String> pathSegments = httpUrl.pathSegments();
        return pathSegments.get(pathSegments.size() - 1);
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            //NO-OP
        }
        return "";
    }
}
