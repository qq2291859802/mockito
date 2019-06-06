/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.verification;

import org.mockito.internal.verification.api.VerificationData;
import org.mockito.verification.VerificationMode;

/**
 * 静态代理封装校验模式
 */
public class MockAwareVerificationMode implements VerificationMode {
    // mock对象
    private final Object mock;
    // 校验模式
    private final VerificationMode mode;

    public MockAwareVerificationMode(Object mock, VerificationMode mode) {
        this.mock = mock;
        this.mode = mode;
    }

    public void verify(VerificationData data) {
        mode.verify(data);
    }

    public Object getMock() {
        return mock;
    }
}