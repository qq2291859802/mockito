/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.internal.invocation;

import org.mockito.internal.InternalMockHandler;
import org.mockito.internal.stubbing.StubbedInvocationMatcher;
import org.mockito.internal.util.MockUtil;
import org.mockito.invocation.Invocation;

import java.util.*;

/**
 * 未使用的测试桩查看器
 */
public class UnusedStubsFinder {

    /**
     * Finds all unused stubs for given mocks
     * 根据mock列表，查看所有的测试桩
     *
     * 
     * @param mocks
     */
    public List<Invocation> find(List<?> mocks) {
        List<Invocation> unused = new LinkedList<Invocation>();
        for (Object mock : mocks) {
            InternalMockHandler<Object> handler = new MockUtil().getMockHandler(mock);
            List<StubbedInvocationMatcher> fromSingleMock = handler.getInvocationContainer().getStubbedInvocations();
            for(StubbedInvocationMatcher s : fromSingleMock) {
                if (!s.wasUsed()) {
                     unused.add(s.getInvocation());
                }
            }
        }
        return unused;
    }
}