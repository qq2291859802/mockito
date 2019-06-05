/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.creation.cglib;

import org.mockito.cglib.core.CodeGenerationException;
import org.mockito.cglib.core.NamingPolicy;
import org.mockito.cglib.core.Predicate;
import org.mockito.cglib.proxy.*;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.internal.creation.instance.InstantationException;
import org.mockito.internal.creation.instance.Instantiator;
import org.mockito.internal.creation.util.SearchingClassLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

import static org.mockito.internal.util.StringJoiner.join;

/**
 * Inspired on jMock (thanks jMock guys!!!)
 */
class ClassImposterizer {

    private final Instantiator instantiator;

    public ClassImposterizer(Instantiator instantiator) {
        this.instantiator = instantiator;
    }
    
    private static final NamingPolicy NAMING_POLICY_THAT_ALLOWS_IMPOSTERISATION_OF_CLASSES_IN_SIGNED_PACKAGES = new MockitoNamingPolicy() {

        /**
         * 类名
         * @param prefix
         * @param source
         * @param key
         * @param names
         * @return
         */
        @Override
        public String getClassName(String prefix, String source, Object key, Predicate names) {
            return "codegen." + super.getClassName(prefix, source, key, names);
        }
    };
    /**
     * 忽略桥接方法
     */
    private static final CallbackFilter IGNORE_BRIDGE_METHODS = new CallbackFilter() {
        public int accept(Method method) {
    /*
        public interface SuperClass<T> {
        void method(T t);
        }

        public class AClass implements SuperClass<String> {
        @Override
        public void method(String s) {
    　　　　System.out.println(s);
    　　}
        }

        ========================================================
        由于泛型擦除，method方法在子类中其实存在两个，分别是：
          public void method(java.lang.String);
          public void method(java.lang.Object);
     */
            return method.isBridge() ? 1 : 0;
        }
    };
    
    public <T> T imposterise(final MethodInterceptor interceptor, Class<T> mockedType, Collection<Class> ancillaryTypes) {
        return imposterise(interceptor, mockedType, ancillaryTypes.toArray(new Class[ancillaryTypes.size()]));
    }
    
    public <T> T imposterise(final MethodInterceptor interceptor, Class<T> mockedType, Class<?>... ancillaryTypes) {
        Class<Factory> proxyClass = null;
        Object proxyInstance = null;
        try {
            setConstructorsAccessible(mockedType, true);
            // 代理的class对象
            proxyClass = createProxyClass(mockedType, ancillaryTypes);
            proxyInstance = createProxy(proxyClass, interceptor);
            return mockedType.cast(proxyInstance);
        } catch (ClassCastException cce) {
            throw new MockitoException(join(
                "ClassCastException occurred while creating the mockito proxy :",
                "  class to mock : " + describeClass(mockedType),
                "  created class : " + describeClass(proxyClass),
                "  proxy instance class : " + describeClass(proxyInstance),
                "  instance creation by : " + instantiator.getClass().getSimpleName(),
                "",
                "You might experience classloading issues, disabling the Objenesis cache *might* help (see MockitoConfiguration)"
            ), cce);
        } finally {
            setConstructorsAccessible(mockedType, false);
        }
    }

    private static String describeClass(Class type) {
        return type == null? "null" : "'" + type.getCanonicalName() + "', loaded by classloader : '" + type.getClassLoader() + "'";
    }

    private static String describeClass(Object instance) {
        return instance == null? "null" : describeClass(instance.getClass());
    }

    /**
     * 设置构造器访问权限
     * @param mockedType
     * @param accessible
     */
    //TODO this method does not belong here
    public void setConstructorsAccessible(Class<?> mockedType, boolean accessible) {
        for (Constructor<?> constructor : mockedType.getDeclaredConstructors()) {
            constructor.setAccessible(accessible);
        }
    }

    /**
     * 创建代理class对象
     * @param mockedType mock接口/对象类型
     * @param interfaces mock的接口
     * @return
     */
    public Class<Factory> createProxyClass(Class<?> mockedType, Class<?>... interfaces) {
        if (mockedType == Object.class) {
            mockedType = ClassWithSuperclassToWorkAroundCglibBug.class;
        }
        
        Enhancer enhancer = new Enhancer() {
            @Override
            @SuppressWarnings("unchecked")
            protected void filterConstructors(Class sc, List constructors) {
                // Don't filter
            }
        };
        // 所有的mock类型(mock类型和接口类型)
        Class<?>[] allMockedTypes = prepend(mockedType, interfaces);
        // 设置类加载器
		enhancer.setClassLoader(SearchingClassLoader.combineLoadersOf(allMockedTypes));
        enhancer.setUseFactory(true);
        if (mockedType.isInterface()) {
            // mock类型是接口
            enhancer.setSuperclass(Object.class);
            enhancer.setInterfaces(allMockedTypes);
        } else {
            enhancer.setSuperclass(mockedType);
            enhancer.setInterfaces(interfaces);
        }
        //
        enhancer.setCallbackTypes(new Class[]{MethodInterceptor.class, NoOp.class});
        enhancer.setCallbackFilter(IGNORE_BRIDGE_METHODS);
        if (mockedType.getSigners() != null) {
            // 类标识存在
            enhancer.setNamingPolicy(NAMING_POLICY_THAT_ALLOWS_IMPOSTERISATION_OF_CLASSES_IN_SIGNED_PACKAGES);
        } else {
            enhancer.setNamingPolicy(MockitoNamingPolicy.INSTANCE);
        }

        enhancer.setSerialVersionUID(42L);
        
        try {
            return enhancer.createClass(); 
        } catch (CodeGenerationException e) {
            if (Modifier.isPrivate(mockedType.getModifiers())) {
                throw new MockitoException("\n"
                        + "Mockito cannot mock this class: " + mockedType 
                        + ".\n"
                        + "Most likely it is a private class that is not visible by Mockito");
            }
            throw new MockitoException("\n"
                    + "Mockito cannot mock this class: " + mockedType 
                    + "\n" 
                    + "Mockito can only mock visible & non-final classes."
                    + "\n" 
                    + "If you're not sure why you're getting this error, please report to the mailing list.", e);
        }
    }

    /**
     * 创建代理对象
     * @param proxyClass
     * @param interceptor
     * @return
     */
    private Object createProxy(Class<Factory> proxyClass, final MethodInterceptor interceptor) {
        Factory proxy;
        try {
            // 代理对象
            proxy = instantiator.newInstance(proxyClass);
        } catch (InstantationException e) {
            throw new MockitoException("Unable to create mock instance of type '" + proxyClass.getSuperclass().getSimpleName() + "'", e);
        }
        // 设置回调
        proxy.setCallbacks(new Callback[] {interceptor, SerializableNoOp.SERIALIZABLE_INSTANCE });
        return proxy;
    }

    /**
     * 合并多个class对象
     * @param first
     * @param rest
     * @return
     */
    private Class<?>[] prepend(Class<?> first, Class<?>... rest) {
        Class<?>[] all = new Class<?>[rest.length+1];
        all[0] = first;
        System.arraycopy(rest, 0, all, 1, rest.length);
        return all;
    }
    
    public static class ClassWithSuperclassToWorkAroundCglibBug {}
    
}