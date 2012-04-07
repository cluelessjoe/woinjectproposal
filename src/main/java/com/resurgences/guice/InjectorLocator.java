package com.resurgences.guice;

import com.google.inject.Injector;

public class InjectorLocator {
    private static Injector injector;

    public static Injector getInjector(){
        return injector;
    }
    
    static void setInjector(Injector injector){
        InjectorLocator.injector = injector;
    }
}
