package com.resurgences.guice;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.TestCase;

import com.resurgences.utils.AssertedFailure;
import com.webobjects.appserver.WOComponent;

public class WOInjectTest extends TestCase {

    public void testTargetAndImplSameClass() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);
        WOInject woInject = new WOInject() {
            @Override
            void assignWonderBinding(Class<?> woClass, Class<?> woBinding) {
                if (woClass.equals(A.class) && woBinding.equals(A.class)) {
                    called.set(true);
                }
                super.assignWonderBinding(woClass, woBinding);
            }
        };
        AbstractWOModule testModule = new AbstractWOModule() {

            @Override
            protected void configure() {
                bind(A.class).to(A.class);

            }
        };
        woInject.createInjector(Collections.singleton(testModule));
        assertTrue(called.get());
    }

    @SuppressWarnings("deprecation")
    public static class A extends WOComponent {

    }

    public static class B extends A {

    }

    public void testCollidingBindings() throws Exception {
        final WOInject woInject = new WOInject();
        final AbstractWOModule testModule = new AbstractWOModule() {

            @Override
            protected void configure() {
                bind(A.class).to(A.class);
                bind(A.class).to(B.class);
            }
        };
        new AssertedFailure(IllegalStateException.class) {

            @Override
            protected void thisMustFail() throws Throwable {
                woInject.createInjector(Collections.singleton(testModule));
            }
        };
    }

    public void testBindingThroughModules() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);
        WOInject woInject = new WOInject() {
            @Override
            void assignWonderBinding(Class<?> woClass, Class<?> woBinding) {
                if (woClass.equals(A.class) && woBinding.equals(A.class)) {
                    called.set(true);
                }
                super.assignWonderBinding(woClass, woBinding);
            }
        };
        AbstractWOModule testModule = new AbstractWOModule() {

            @Override
            protected void configure() {
                install(new AbstractWOModule() {

                    @Override
                    protected void configure() {
                        bind(A.class).to(A.class);
                    }
                });
            };

        };
        woInject.createInjector(Collections.singleton(testModule));
        assertTrue(called.get());

    }

    public void testBindingToSelf() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);
        WOInject woInject = new WOInject() {
            @Override
            void assignWonderBinding(Class<?> woClass, Class<?> woBinding) {
                if (woClass.equals(A.class)) {
                    called.set(true);
                }
                super.assignWonderBinding(woClass, woBinding);
            }
        };
        AbstractWOModule testModule = new AbstractWOModule() {

            @Override
            protected void configure() {
                bind(A.class);

            }
        };
        woInject.createInjector(Collections.singleton(testModule));
        assertTrue(called.get());
    }
}
