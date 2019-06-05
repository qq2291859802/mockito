/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.verification;

import org.mockito.internal.util.collections.IdentitySet;
import org.mockito.internal.verification.api.InOrderContext;
import org.mockito.invocation.Invocation;

/**
 * 依靠IdentitySet保证添加Invocation对象的唯一性
 */
public class InOrderContextImpl implements InOrderContext {
    
    final IdentitySet verified = new IdentitySet();

    public boolean isVerified(Invocation invocation) {
        return verified.contains(invocation);
    }

    public void markVerified(Invocation i) {
        verified.add(i);
    }
}