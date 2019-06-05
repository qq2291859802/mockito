/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.internal.creation.cglib;

import org.mockito.cglib.proxy.MethodInterceptor;
import org.mockito.cglib.proxy.MethodProxy;
import org.mockito.internal.InternalMockHandler;
import org.mockito.internal.creation.DelegatingMethod;
import org.mockito.internal.creation.util.MockitoMethodProxy;
import org.mockito.internal.invocation.InvocationImpl;
import org.mockito.internal.invocation.MockitoMethod;
import org.mockito.internal.invocation.SerializableMethod;
import org.mockito.internal.invocation.realmethod.CleanTraceRealMethod;
import org.mockito.internal.progress.SequenceNumber;
import org.mockito.internal.util.ObjectMethodsGuru;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.MockHandler;
import org.mockito.mock.MockCreationSettings;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Should be one instance per mock instance, see CglibMockMaker.
 *
 * 当执行mock对象的方法时，会执行org.mockito.internal.creation.cglib.MethodInterceptorFilter#intercept
 */
class MethodInterceptorFilter implements MethodInterceptor, Serializable {

    private static final long serialVersionUID = 6182795666612683784L;
    private final InternalMockHandler handler;
    final ObjectMethodsGuru objectMethodsGuru = new ObjectMethodsGuru();
    // 配置对象
    private final MockCreationSettings mockSettings;
    private final AcrossJVMSerializationFeature acrossJVMSerializationFeature = new AcrossJVMSerializationFeature();

    public MethodInterceptorFilter(InternalMockHandler handler, MockCreationSettings mockSettings) {
        this.handler = handler;
        this.mockSettings = mockSettings;
    }

    /**
     * 在mock对象执行方法时调用，用于动态替换方法执行逻辑
     * @param proxy 代理对象
     * @param method 被执行的方法对象，如果直接调用method.invoke(args)表示原有方法逻辑不改变
     * @param args 被执行方法的参数
     * @param methodProxy 方法的代理对象
     * @return
     * @throws Throwable
     */
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy)
            throws Throwable {
        if (objectMethodsGuru.isEqualsMethod(method)) {
            return proxy == args[0];
        } else if (objectMethodsGuru.isHashCodeMethod(method)) {
            return hashCodeForMock(proxy);
        } else if (acrossJVMSerializationFeature.isWriteReplace(method)) {
            return acrossJVMSerializationFeature.writeReplace(proxy);
        }

        MockitoMethodProxy mockitoMethodProxy = createMockitoMethodProxy(methodProxy);
        // 修改命名协议
        new CGLIBHacker().setMockitoNamingPolicy(methodProxy);
        
        MockitoMethod mockitoMethod = createMockitoMethod(method);
        // 增加部分日志过滤功能
        CleanTraceRealMethod realMethod = new CleanTraceRealMethod(mockitoMethodProxy);
        // 创建一个调用器对象
        Invocation invocation = new InvocationImpl(proxy, mockitoMethod, args, SequenceNumber.next(), realMethod);
        return handler.handle(invocation);
    }
   
    public MockHandler getHandler() {
        return handler;
    }

    private int hashCodeForMock(Object mock) {
        return System.identityHashCode(mock);
    }

    /**
     * 封装methodProxy对象
     * @param methodProxy
     * @return
     */
    public MockitoMethodProxy createMockitoMethodProxy(MethodProxy methodProxy) {
        if (mockSettings.isSerializable())
            return new SerializableMockitoMethodProxy(methodProxy);
        return new DelegatingMockitoMethodProxy(methodProxy);
    }

    /**
     * 封装method对象
     * @param method
     * @return
     */
    public MockitoMethod createMockitoMethod(Method method) {
        if (mockSettings.isSerializable()) {
            return new SerializableMethod(method);
        } else {
            return new DelegatingMethod(method);
        }
    }
}