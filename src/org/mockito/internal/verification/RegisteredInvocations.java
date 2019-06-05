/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.internal.verification;

import org.mockito.internal.util.ObjectMethodsGuru;
import org.mockito.internal.util.collections.ListUtil;
import org.mockito.internal.util.collections.ListUtil.Filter;
import org.mockito.invocation.Invocation;

import java.util.List;

/**
 * Invocation管理
 */
public interface RegisteredInvocations {

    /**
     * 添加一个Invocation对象
     * @param invocation
     */
    void add(Invocation invocation);

    /**
     * 移除最后一个Invocation对象
     */
    void removeLast();

    /**
     * 获取所有的Invocation对象
     * @return
     */
    List<Invocation> getAll();

    /**
     * 是否为空
     * @return
     */
    boolean isEmpty();

}
