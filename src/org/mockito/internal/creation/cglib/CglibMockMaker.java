/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.creation.cglib;

import org.mockito.cglib.proxy.Callback;
import org.mockito.cglib.proxy.Factory;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.internal.InternalMockHandler;
import org.mockito.internal.creation.instance.InstantiatorProvider;
import org.mockito.invocation.MockHandler;
import org.mockito.mock.MockCreationSettings;
import org.mockito.plugins.MockMaker;

/**
 * A MockMaker that uses cglib to generate mocks on a JVM.
 *
 * 使用cglib创建mock对象（改变原有的类/接口的处理逻辑）
 */
public class CglibMockMaker implements MockMaker {

    public <T> T createMock(MockCreationSettings<T> settings, MockHandler handler) {
        InternalMockHandler mockitoHandler = cast(handler);
        new AcrossJVMSerializationFeature().enableSerializationAcrossJVM(settings);
        // 创建mock对象
        return new ClassImposterizer(new InstantiatorProvider().getInstantiator(settings)).imposterise(
                // MethodInterceptorFilter会改变原有方法的行为
                new MethodInterceptorFilter(mockitoHandler, settings), settings.getTypeToMock(), settings.getExtraInterfaces());
    }

    /**
     * 校验是不是InternalMockHandler类型
     * @param handler
     * @return
     */
    private InternalMockHandler cast(MockHandler handler) {
        if (!(handler instanceof InternalMockHandler)) {
            throw new MockitoException("At the moment you cannot provide own implementations of MockHandler." +
                    "\nPlease see the javadocs for the MockMaker interface.");
        }
        return (InternalMockHandler) handler;
    }

    /**
     * 重置mock
     * @param mock The mock instance whose invocation handler is to be replaced.
     * @param newHandler The new invocation handler instance.
     * @param settings The mock settings - should you need to access some of the mock creation details.
     */
    public void resetMock(Object mock, MockHandler newHandler, MockCreationSettings settings) {
        ((Factory) mock).setCallback(0, new MethodInterceptorFilter(cast(newHandler), settings));
    }

    public MockHandler getHandler(Object mock) {
        if (!(mock instanceof Factory)) {
            return null;
        }
        Factory factory = (Factory) mock;
        Callback callback = factory.getCallback(0);
        if (!(callback instanceof MethodInterceptorFilter)) {
            return null;
        }
        return ((MethodInterceptorFilter) callback).getHandler();
    }
}
