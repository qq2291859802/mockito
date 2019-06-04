/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.internal.invocation.finder;

import org.mockito.internal.util.collections.ListUtil;
import org.mockito.internal.util.collections.ListUtil.Filter;
import org.mockito.invocation.Invocation;

import java.util.List;

/**
 * Author: Szczepan Faber, created at: 4/3/11
 */
public class VerifiableInvocationsFinder {

    public List<Invocation> find(List<?> mocks) {
        List<Invocation> invocations = new AllInvocationsFinder().find(mocks);
        return ListUtil.filter(invocations, new RemoveIgnoredForVerification());
    }

    private static class RemoveIgnoredForVerification implements Filter<Invocation>{
        // 移除忽略校验的调用器
        public boolean isOut(Invocation invocation) {
            return invocation.isIgnoredForVerification();
        }
    }
}
