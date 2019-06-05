/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.creation.cglib;

import org.mockito.cglib.proxy.MethodProxy;
import org.mockito.internal.creation.util.MockitoMethodProxy;

/**
 * 静态代理方式，修改methodProxy对象的默认行为
 */
class DelegatingMockitoMethodProxy implements MockitoMethodProxy {

    private final MethodProxy methodProxy;

    public DelegatingMockitoMethodProxy(MethodProxy methodProxy) {
        this.methodProxy = methodProxy;
    }

    public Object invokeSuper(Object target, Object[] arguments) throws Throwable {
        return methodProxy.invokeSuper(target, arguments);
    }
}