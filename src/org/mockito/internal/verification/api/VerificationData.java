/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.verification.api;

import java.util.List;

import org.mockito.internal.invocation.InvocationMatcher;
import org.mockito.invocation.Invocation;

/**
 * 校验数据对象(校验是针对mock类)
 */
public interface VerificationData {

    /**
     * mock对象包含多个invocation对象
     * @return
     */
    List<Invocation> getAllInvocations();

    /**
     * 通过匹配器判断想要的结果
     * @return
     */
    InvocationMatcher getWanted();   
    
}