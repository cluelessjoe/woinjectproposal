package com.resurgences.guice;

import java.util.HashSet;
import java.util.Set;

import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.resurgences.utils.AssertUtils;

public abstract class AbstractWOModule extends com.google.inject.AbstractModule {
    protected static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AbstractWOModule.class);
    private final Set<WOBindingBuilder<?>> wonderBindings = new HashSet<WOBindingBuilder<?>>();
    private final Set<AbstractWOModule> innerModules = new HashSet<AbstractWOModule>();

    @Override
    protected <T> AnnotatedBindingBuilder<T> bind(Class<T> type) {
        AssertUtils.assertParametersNotNull("type", type);
        if (isWonderClass(type)) {
            return wonderBind(type);
        } else {
            return super.bind(type);
        }
    }

    @Override
    protected <T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral) {
        AssertUtils.assertParametersNotNull("typeLiteral", typeLiteral);
        if (isWonderClass(typeLiteral)) {
            return wonderBind(typeLiteral);
        } else {

            return super.bind(typeLiteral);
        }
    }

    protected void install(AbstractWOModule module) {
        super.install(module);
        innerModules.add(module);
    }

    @Override
    protected <T> LinkedBindingBuilder<T> bind(Key<T> key) {
        AssertUtils.assertParametersNotNull("key", key);
        if (isWonderClass(key)) {
            return wonderBind(key);
        } else {

            return super.bind(key);
        }
    }

    public boolean isWonderClass(TypeLiteral<?> typeLiteral) {
        return isWonderClass(typeLiteral.getRawType());
    }

    public boolean isWonderClass(Class<?> type) {
        return WOInject.isWonderClass(type);
    }

    public boolean isWonderClass(Key<?> key) {
        return isWonderClass(key.getTypeLiteral().getRawType());
    }

    private <T> AnnotatedBindingBuilder<T> wonderBind(Class<T> type) {
        return wonderBind(Key.get(type));
    }

    private <T> AnnotatedBindingBuilder<T> wonderBind(TypeLiteral<T> type) {
        return wonderBind(Key.get(type));
    }

    private <T> AnnotatedBindingBuilder<T> wonderBind(Key<T> type) {
        WOBindingBuilder bindingBuilder = new WOBindingBuilder(type);
        wonderBindings.add(bindingBuilder);
        return bindingBuilder;
    }

    public Set<WOBindingBuilder<?>> getWonderBindings() {
        HashSet<WOBindingBuilder<?>> effectiveBindings = new HashSet<WOBindingBuilder<?>>();
        effectiveBindings.addAll(wonderBindings);
        for (AbstractWOModule innerModule : innerModules) {
            effectiveBindings.addAll(innerModule.getWonderBindings());
        }
        return effectiveBindings;
    }

    @Override
    protected void install(Module module) {
        if (module instanceof AbstractWOModule == false) {
            throw new IllegalStateException("The module '" + module + "' is invalid: only '" + AbstractWOModule.class
                    + "' sub modules should be used for bindings.");
        }
        super.install(module);
    }
}
