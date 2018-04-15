package com.winwin.common.rxcache;

import android.support.annotation.Nullable;
import android.util.Log;

import com.winwin.common.rxcache.annotation.Read;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by Jason on 2018/4/15.
 */

public class RxCache {

    public <T> T create(final Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, @Nullable Object[] args)
                            throws Throwable {
                        // If the method is a method from Object then defer to normal invocation.
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }
                        parseMethod(method);
                        return new Object();
                    }
                });
    }

    private void parseMethod(Method method) {
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            parseAnnotation(annotation);
        }
    }

    private void parseAnnotation(Annotation annotation) {
        if (annotation instanceof Read) {
            String key = ((Read) annotation).key();
            Class type = ((Read) annotation).type();
            Log.d("TAG", "parseAnnotation: ");
        }
    }

}
