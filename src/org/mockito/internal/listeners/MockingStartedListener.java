/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.listeners;

import org.mockito.MockSettings;

/**
 * mock开始的监听器
 */
@SuppressWarnings("unchecked")
public interface MockingStartedListener extends MockingProgressListener {
    /**
     * mock开始
     * @param mock
     * @param classToMock
     */
    void mockingStarted(Object mock, Class classToMock);
}
