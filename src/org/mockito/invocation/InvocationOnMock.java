/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.invocation;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 *
 * mock的调用对象
 *
 *
 * An invocation on a mock
 * <p>
 * A placeholder for mock, the method that was called and the arguments that were passed.
 */
public interface InvocationOnMock extends Serializable {

    /**
     * returns the mock object 
     * 
     * @return mock object
     */
    Object getMock();

    /**
     * returns the method
     * 
     * @return method
     */
    Method getMethod();

    /**
     * returns arguments passed to the method
     * 
     * @return arguments
     */
    Object[] getArguments();
    
    /**
     * 指定类型（存在类型转换）和下标的参数对象
     *
    * Returns casted argument using position
    * @param index argument position
    * @param clazz argument type
    * @return casted argument on position
    */
    <T> T getArgumentAt(int index, Class<T> clazz);


    /**
     *
     * 执行方法
     * calls real method
     * <p>
     * <b>Warning:</b> depending on the real implementation it might throw exceptions  
     *
     * @return whatever the real method returns / throws
     * @throws Throwable in case real method throws 
     */
    Object callRealMethod() throws Throwable;
}
