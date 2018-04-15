package com.winwin.common.rxcache.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Jason on 2018/4/15.
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface Read {

    String key();

    Class type();

}
