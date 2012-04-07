package com.resurgences.guice;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Loader;
import javassist.LoaderClassPath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.ImplementedBy;
import com.google.inject.Injector;
import com.resurgences.utils.AssertUtils;
import com.resurgences.utils.ExceptionUtils;
import com.webobjects.appserver.WOElement;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.foundation._NSUtilities;

public class WOInject {
    private static final String INJECT_MEMBER_HOOK = "{ com.resurgences.guice.InterceptorHook.injectMembers($_); }";

    protected static final Logger log = LoggerFactory.getLogger(WOInject.class);

    private final Map<Class<?>, Class<?>> wonderBindings = new HashMap<Class<?>, Class<?>>();

    public Injector createInjector(Set<AbstractWOModule> modules) {
        AssertUtils.assertParametersNotNull("modules", modules);
        AbstractWOModule[] effectiveModules = createEffectiveModules(modules);
        Injector injector = Guice.createInjector(effectiveModules);
        gatherWonderBindings(effectiveModules);
        assignWonderBindings();
        return injector;
    }

    private AbstractWOModule[] createEffectiveModules(Set<AbstractWOModule> set) {
                HashSet<AbstractWOModule> modules = new HashSet<AbstractWOModule>(set.size() + 1);
        modules.addAll(set);
        modules.add(new AbstractWOModule() {

            @Override
            protected void configure() {
                bind(WOInject.class).toInstance(WOInject.this);
                bind(EOGenericRecord.class).to(EOGenericRecord.class);
            }
        });
        return modules.toArray(new AbstractWOModule[] {});
    }

    public static void interceptWonderObjectCreation(ClassPool cp) {

        try {
            cp.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));

            CtClass cc = cp.get("com.webobjects.foundation._NSUtilities");

            CtMethod m = cc.getDeclaredMethod("instantiateObject");
            m.insertAfter(INJECT_MEMBER_HOOK);

            m = cc.getDeclaredMethod("instantiateObjectWithConstructor");
            m.insertAfter(INJECT_MEMBER_HOOK);

        } catch (Throwable exception) {
            throw new RuntimeException("Cannot intercept wonder object creation.", exception);
        }
    }

    private void assignWonderBindings() {
        Set<Entry<Class<?>, Class<?>>> bindings = wonderBindings.entrySet();
        for (Entry<Class<?>, Class<?>> entry : bindings) {
            Class<?> key = entry.getKey();
            Class<?> value = entry.getValue();
            assignWonderBinding(key,value);
         }
    }


    private void gatherWonderBindings(AbstractWOModule... modules) {
        for (AbstractWOModule wonderModule : modules) {
            Set<WOBindingBuilder<?>> bindings = wonderModule.getWonderBindings();
            for (WOBindingBuilder<?> wonderBindingBuilder : bindings) {
                Class<?> woType = wonderBindingBuilder.getType().getTypeLiteral().getRawType();
                Class<?> woImpl = wonderBindingBuilder.getImplementation();
                addWOBindingDefinition(woType, woImpl);
            }
        }
    }

    public static void main(String[] args, String applicationClass) {
        ClassPool cp = ClassPool.getDefault();
        interceptWonderObjectCreation(cp);
        startGuiceApplication(cp, args, applicationClass);
    }

    private static void startGuiceApplication(ClassPool cp, String[] args, String applicationClass) {
        Loader cl = new Loader(Thread.currentThread().getContextClassLoader(), cp);

        Thread.currentThread().setContextClassLoader(cl);
        try {
            Class<?> erxApplication = cl.loadClass("er.extensions.appserver.ERXApplication");

            Class<?> appClass = cl.loadClass(applicationClass);

            Class<?> injectableAppClass = cl.loadClass("com.resurgences.guice.GuiceApplication");

            if (!injectableAppClass.isAssignableFrom(appClass)) {
                throw new RuntimeException("Cannot initialize the injector. The Application class doesn't extend GuiceApplication.");
            }

            erxApplication.getDeclaredMethod("main", String[].class, Class.class).invoke(null, args, appClass);
        } catch (Throwable e) {
            throw new RuntimeException("Cannot initialize the application to take advantage of WOInject features.", e);
        }
    }

    public static boolean isWonderClass(Class<?> type) {
        return EOEnterpriseObject.class.isAssignableFrom(type) || WOElement.class.isAssignableFrom(type);
    }

    void assignWonderBinding(Class<?> woClass, Class<?> woBinding) {
        Class<?> effectiveWoBinding = woBinding != null ? woBinding : woClass;
        _NSUtilities.setClassForName(effectiveWoBinding, woClass.getSimpleName());
        _NSUtilities.setClassForName(effectiveWoBinding, woClass.getName());
    }

    private void addWOBindingDefinition(Class<?> woClass, Class<?> woImpl) {
        if (wonderBindings.containsKey(woClass) && wonderBindings.get(woClass).equals(woImpl) == false) {
            throw new java.lang.IllegalStateException("Trying to bind '" + woClass.getName() + "' to '"
                    + woImpl.getName() + "', but a binding already exists to '" + wonderBindings.get(woClass) + "'.");
        }
        log.info("WO Type '" + woClass + "' bound to impl '" + woImpl + "'");
        wonderBindings.put(woClass, woImpl);
    }

    private Class<? extends EOGenericRecord> getLateEntityBinding(Class<? extends EOGenericRecord> woClass) {
        if (woClass.isAnnotationPresent(ImplementedBy.class)) {
            ImplementedBy annotation = woClass.getAnnotation(ImplementedBy.class);
            return (Class<? extends EOGenericRecord>) annotation.value();
        }
        return woClass;
    }

    public void bindEntities(Enumeration<EOEntity> entities) {
        while(entities.hasMoreElements()){
            EOEntity entity = entities.nextElement();
            Class<? extends EOGenericRecord> entityTargetClass = getEntityTargetClass(entity);
            Class<? extends EOGenericRecord> entityImpl = getEntityBinding(entityTargetClass);
            String implClassName = entityImpl.getName();
            entity.setClassName(implClassName);
            log.info("Bound  entity '" + entity.name() + "' to '" + implClassName + "'.");
        }
    }

    private Class<? extends EOGenericRecord> getEntityBinding(Class<? extends EOGenericRecord> entityTargetClass) {
        Class<?> implClass = wonderBindings.get(entityTargetClass);
        if (implClass != null) {
            return (Class<? extends EOGenericRecord>) implClass;
        }
        Class<? extends EOGenericRecord> lateEntityBinding = getLateEntityBinding(entityTargetClass);
        addWOBindingDefinition(entityTargetClass, lateEntityBinding);
        assignWonderBinding(entityTargetClass, lateEntityBinding);
        return lateEntityBinding;
    }

    private Class<? extends EOGenericRecord> getEntityTargetClass(EOEntity entity) {
        String className = entity.className();
        AssertUtils.assertNotNull(className, "className");
        if (EOGenericRecord.class.getSimpleName().equals(className)) {
            return EOGenericRecord.class;
        }
        try {
            return (Class<? extends EOGenericRecord>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw ExceptionUtils.wrap(e);
        }
    }
}
