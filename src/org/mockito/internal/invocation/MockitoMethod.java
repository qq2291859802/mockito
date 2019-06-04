/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.invocation;

import java.lang.reflect.Method;

/**
 * 抽象的方法对象
 */
public interface MockitoMethod extends AbstractAwareMethod {
    /**
     * 方法名
     * @return
     */
    public String getName();

    /**
     * 返回值类型
     * @return
     */
    public Class<?> getReturnType();

    /**
     * 参数类型列表
     * @return
     */
    public Class<?>[] getParameterTypes();

    /**
     * 异常列表
     * @return
     */
    public Class<?>[] getExceptionTypes();

    /**
     * 是否是可变参数
     * @return
     */
    public boolean isVarArgs();

    /**
     * 对应java的方法对象
     * @return
     */
    public Method getJavaMethod();
}
