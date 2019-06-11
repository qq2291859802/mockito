/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util.collections;


import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * set工具类
 */
public abstract class Sets {
    /**
     * 创建一个hashcode和equals安全的集合
     * @param mocks
     * @return
     */
    public static Set<Object> newMockSafeHashSet(Iterable<Object> mocks) {
        return HashCodeAndEqualsSafeSet.of(mocks);
    }
    /**
     * 创建一个hashcode和equals安全的集合
     * @param mocks
     * @return
     */
    public static Set<Object> newMockSafeHashSet(Object... mocks) {
        return HashCodeAndEqualsSafeSet.of(mocks);
    }

    public static IdentitySet newIdentitySet() {
        return new IdentitySet();
    }

    public static <T> Set<T> newSet(T ... elements) {
        if (elements == null) {
            throw new IllegalArgumentException("Expected an array of elements (or empty array) but received a null.");
        }
        return new LinkedHashSet<T>(asList(elements));
    }
}
