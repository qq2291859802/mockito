/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.verification.api;

import org.mockito.invocation.Invocation;

public interface InOrderContext {

    /**
     * 是否已经校验了
     * @param invocation
     * @return
     */
    boolean isVerified(Invocation invocation);

    /**
     * 标记校验
     * @param i
     */
    void markVerified(Invocation i);

}
