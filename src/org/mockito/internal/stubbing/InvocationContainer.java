/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.stubbing;

import org.mockito.invocation.Invocation;

import java.util.List;

/**
 * invocation容器
 */
//TODO move to different package
public interface InvocationContainer {
    /**
     * 获取所有的Incocation列表（一个mock对象有多个Invocation对象(一般方法和Invocation对象是一一对应的)）
     * @return
     */
    List<Invocation> getInvocations();

    /**
     *
     * @return
     */
    List<StubbedInvocationMatcher> getStubbedInvocations();
}
