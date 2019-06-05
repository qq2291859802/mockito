/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.internal.exceptions.stacktrace;

import org.mockito.configuration.IMockitoConfiguration;
import org.mockito.internal.configuration.GlobalConfiguration;

import java.io.Serializable;

/**
 * 根据条件清除部分堆栈日志
 */
public class ConditionalStackTraceFilter implements Serializable {
    private static final long serialVersionUID = -8085849703510292641L;
    
    private final IMockitoConfiguration config = new GlobalConfiguration();
    private final StackTraceFilter filter = new StackTraceFilter();

    /**
     * 清除异常对象中的部分堆栈信息
     * @param throwable
     */
    public void filter(Throwable throwable) {
        if (!config.cleansStackTrace()) {
            // 是否需要清除堆栈
            return;
        }

        StackTraceElement[] filtered = filter.filter(throwable.getStackTrace(), true);
        // 修改异常的堆栈信息
        throwable.setStackTrace(filtered);
    }
}