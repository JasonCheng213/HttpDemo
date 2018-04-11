package com.winwin.common.http.convert;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.annotations.Nullable;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 文件转换工厂类
 * Created by Jason on 2017/9/10.
 */

public class FileConvertFactory extends Converter.Factory {

    public static FileConvertFactory create() {
        return new FileConvertFactory();
    }

    @Nullable
    @Override
    public Converter<ResponseBody, File> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (type == File.class)
            return new FileResponseBodyConverter();
        else
            return null;
    }
}
