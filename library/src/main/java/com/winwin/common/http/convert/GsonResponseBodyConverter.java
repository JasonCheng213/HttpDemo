package com.winwin.common.http.convert;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;
    private final Type type;

    GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter, Type type) {
        this.gson = gson;
        this.adapter = adapter;
        this.type = type;
    }

    @Override
    public T convert(@NonNull ResponseBody value) throws IOException {
        T result = null;
        try {
            if (type == String.class) {
                //noinspection unchecked
                result = (T) value.string();
            } else {
                JsonReader jsonReader = gson.newJsonReader(value.charStream());
                result = adapter.read(jsonReader);
            }
        } finally {
            value.close();
        }
        return result;
    }
}
