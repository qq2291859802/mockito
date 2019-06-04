/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.internal.matchers;

import java.io.Serializable;

/**
 * 等于匹配
 * @param <T>
 */
public class CompareEqual<T extends Comparable<T>> extends CompareTo<T> implements Serializable {

    private static final long serialVersionUID = 2998586260452920429L;

    public CompareEqual(Comparable<T> value) {
        super(value);
    }

    @Override
    protected String getName() {
        return "cmpEq";
    }

    @Override
    protected boolean matchResult(int result) {
        return result == 0;
    }
}
