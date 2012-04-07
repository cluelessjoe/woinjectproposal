package com.resurgences.utils;


public class ExceptionUtils {

    public static RuntimeException wrap(Throwable e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else {
            return new RuntimeException(e);
        }
    }

}
