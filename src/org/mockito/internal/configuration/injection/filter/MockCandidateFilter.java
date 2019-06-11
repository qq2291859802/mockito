/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.configuration.injection.filter;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * 注入mock列表时，可能会存在一些没有用的mock实例。通过这个类进行过滤
 */
public interface MockCandidateFilter {

    /**
     * 过滤有效的mock候选列表
     * @param mocks
     * @param fieldToBeInjected
     * @param fieldInstance
     * @return
     */
    OngoingInjecter filterCandidate(
            Collection<Object> mocks,
            Field fieldToBeInjected,
            Object fieldInstance
    );

}
