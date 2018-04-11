package com.winwin.common.http.convert;


import com.winwin.common.http.response.ProgressResponseBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

import io.reactivex.annotations.NonNull;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * 请求结果转为文件保存
 * Created by Jason on 2017/9/10.
 */

class FileResponseBodyConverter implements Converter<ResponseBody, File> {

    FileResponseBodyConverter() {
    }

    @Override
    public File convert(@NonNull ResponseBody value) throws IOException {
        return writeResponseBodyToDisk(value, getSaveFilePath(value));
    }

    private String getSaveFilePath(ResponseBody value) {
        String saveFilePath = null;
        //noinspection TryWithIdenticalCatches
        try {
            Class aClass = value.getClass();
            Field field = aClass.getDeclaredField("delegate");
            field.setAccessible(true);
            ResponseBody body = (ResponseBody) field.get(value);
            if (body instanceof ProgressResponseBody) {
                ProgressResponseBody prBody = ((ProgressResponseBody) body);
                saveFilePath = prBody.getFilePath();
            }
        } catch (NoSuchFieldException e) {
            //NO-OP
        } catch (IllegalAccessException e) {
            //NO-OP
        }
        return saveFilePath;
    }

    private File writeResponseBodyToDisk(ResponseBody body, String path) throws IOException {
        File file = new File(path);
        InputStream inputStream = body.byteStream();
        OutputStream outputStream = new FileOutputStream(file);
        byte[] fileReader = new byte[4096];
        while (true) {
            int read = inputStream.read(fileReader);
            if (read != -1) {
                outputStream.write(fileReader, 0, read);
            } else {
                break;
            }
        }
        outputStream.flush();
        inputStream.close();
        outputStream.close();
        return file;
    }
}
