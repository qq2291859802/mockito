/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util;

/**
 * 日志的抽象类
 */
public interface MockitoLogger {

    /**
     * 打印日志
     * @param what
     */
    void log(Object what);

}