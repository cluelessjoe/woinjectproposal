package com.resurgences.guice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

import javax.inject.Scope;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import com.resurgences.utils.AssertUtils;

public class WOBindingBuilder<T> implements AnnotatedBindingBuilder<T> {

    private final Key<T> type;

    public Class<? extends T> getImplementation() {
        return (Class<? extends T>) (implementation != null ? implementation: type.getTypeLiteral().getRawType());
    }

    private Class<? extends T> implementation;

    public WOBindingBuilder(Key<T> type) {
        this.type = type;
    }

    public ScopedBindingBuilder to(Class<? extends T> impl) {
        AssertUtils.assertParametersNotNull("impl", impl);
        this.implementation = impl;
        return this;
    }

    public ScopedBindingBuilder to(TypeLiteral<? extends T> impl) {
        return to((Class<? extends T>) impl.getRawType());
    }

    public ScopedBindingBuilder to(Key<? extends T> targetKey) {
        return to(targetKey.getTypeLiteral());
    }

    public void toInstance(T instance) {
        throw new IllegalStateException("A wonder binding for '" + getType()
                + "'  can't be achieved through an instance");
    }

    public <S extends T> ScopedBindingBuilder toConstructor(Constructor<S> constructor) {
        throw new IllegalStateException("A wonder binding for '" + getType()
                + "'  can't be achieved through a constructor");
    }

    public <S extends T> ScopedBindingBuilder toConstructor(Constructor<S> constructor, TypeLiteral<? extends S> typeLit) {
        throw new IllegalStateException("A wonder binding for '" + typeLit
                + "'  can't be achieved through a constructor");
    }

    public void in(Class<? extends Annotation> scopeAnnotation) {
        throw new IllegalStateException("A wonder binding for '" + getType() + "'  can't be scoped");
    }

    public void in(Scope scope) {
        throw new IllegalStateException("A wonder binding for '" + getType() + "'  can't be scoped");

    }

    public void asEagerSingleton() {
        throw new IllegalStateException("A wonder binding for '" + getType() + "'  can't be a singleton");

    }

    public LinkedBindingBuilder<T> annotatedWith(Class<? extends Annotation> annotationType) {
        throw new IllegalStateException("A wonder binding for '" + getType() + "'  can't be annotated");
    }

    public LinkedBindingBuilder<T> annotatedWith(Annotation annotation) {
        throw new IllegalStateException("A wonder binding for '" + getType() + "'  can't be annotated");
    }

    public Key<T> getType() {
        return type;
    }
    
    public ScopedBindingBuilder toProvider(Class<? extends javax.inject.Provider<? extends T>> arg0) {
        throw new IllegalStateException("A wonder binding for '" + getType() + "'  can't be achieved through a provider");
    }

    public ScopedBindingBuilder toProvider(TypeLiteral<? extends javax.inject.Provider<? extends T>> arg0) {
        throw new IllegalStateException("A wonder binding for '" + getType() + "'  can't be achieved through a provider");    }

    public ScopedBindingBuilder toProvider(Key<? extends javax.inject.Provider<? extends T>> arg0) {
        throw new IllegalStateException("A wonder binding for '" + getType() + "'  can't be achieved through a provider");
    }

    public void in(com.google.inject.Scope scope) {
        throw new IllegalStateException("A wonder binding for '" + getType() + "'  can't be annotated");
    }

    public ScopedBindingBuilder toProvider(com.google.inject.Provider<? extends T> provider) {
        throw new IllegalStateException("A wonder binding for '" + getType() + "'  can't be annotated");
    }

}
