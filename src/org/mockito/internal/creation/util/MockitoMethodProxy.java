/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.creation.util;

//TODO SF Replace with RealMethod and get rid of (possibly).
public interface MockitoMethodProxy {
    /**
     * 方法调用
     * @param target 方法所属对象
     * @param arguments 方法实参列表
     * @return
     * @throws Throwable
     */
    Object invokeSuper(Object target, Object[] arguments) throws Throwable;
}