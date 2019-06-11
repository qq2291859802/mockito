/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.configuration.injection.filter;

/**
 * 
 *
 * Allow the ongoing injection of a mock candidate.
 */
public interface OngoingInjecter {

    /**
     * Inject the mock.
     *
     * 注入的mock对象
     *
     * <p>
     * Please check the actual implementation.
     * </p>
     *
     * @return the mock that was injected, <code>null</code> otherwise.
     */
    Object thenInject();

}
