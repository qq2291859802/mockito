/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.invocation.realmethod;

public interface RealMethod {
    /**
     * 方法执行
     * @param target
     * @param arguments
     * @return
     * @throws Throwable
     */
    Object invoke(Object target, Object[] arguments) throws Throwable;

}
