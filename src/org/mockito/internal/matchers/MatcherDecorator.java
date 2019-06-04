/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.matchers;

import java.io.Serializable;

import org.hamcrest.Matcher;

/**
 * 匹配装饰器
 */
@SuppressWarnings("unchecked")
public interface MatcherDecorator extends Serializable {
    /**
     * 实际的匹配器
     * @return
     */
    Matcher getActualMatcher();
}
