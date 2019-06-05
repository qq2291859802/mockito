/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.invocation.realmethod;

import org.mockito.internal.creation.util.MockitoMethodProxy;

import java.io.Serializable;

/**
 * 静态代理，封装MockitoMethodProxy对象
 * MockitoMethodProxy使用静态代理的方式修改methodProxy的行为，RealMethod修改MockitoMethodProxy
 */
public class DefaultRealMethod implements RealMethod, Serializable {

    private static final long serialVersionUID = -4596470901191501582L;
    private final MockitoMethodProxy methodProxy;

    public DefaultRealMethod(MockitoMethodProxy methodProxy) {
        this.methodProxy = methodProxy;
    }

    public Object invoke(Object target, Object[] arguments) throws Throwable {
        return methodProxy.invokeSuper(target, arguments);
    }
}
