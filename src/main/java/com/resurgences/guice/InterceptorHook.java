package com.resurgences.guice;

public class InterceptorHook {
    private static Interceptor interceptor;

    public static void setInterceptor(Interceptor interceptor) {
        if (interceptor == null) {
            throw new IllegalStateException("Interceptor already defined '" + interceptor + "'");
        }
        InterceptorHook.interceptor = interceptor;
    }

    public static void injectMembers(Object o) {
        getInterceptor().injectMembers(o);
    }

    private static Interceptor getInterceptor() {
        if (interceptor == null) {
            interceptor = new Interceptor();
        }
        return interceptor;
    }

}
