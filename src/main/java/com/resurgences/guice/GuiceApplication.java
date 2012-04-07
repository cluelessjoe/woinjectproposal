package com.resurgences.guice;

import java.util.Set;

import com.google.inject.Injector;
import com.resurgences.utils.LogUtils;
import com.webobjects.appserver.WOApplication;

import er.extensions.appserver.ERXApplication;

public abstract class GuiceApplication extends ERXApplication {

    private final Injector injector;

    public static GuiceApplication application() {
        return (GuiceApplication) WOApplication.application();
    }

    public GuiceApplication() {
        LogUtils.initLog();//quick fix to make it all compile        
        injector = createInjector();
        InjectorLocator.setInjector(getInjector());
        injector.injectMembers(this);
    }

    private Injector createInjector() {
        WOInject woGuice = new WOInject();
        return woGuice.createInjector(getModules());
    }

    protected abstract Set<AbstractWOModule> getModules();

    public Injector getInjector() {
        return injector;
    }

}
