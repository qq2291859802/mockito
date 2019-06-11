/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.internal.util;

/**
 * Pre-made preconditions
 *
 * 校验器
 */
public class Checks {

    /**
     * 校验不能为空
     * @param value
     * @param checkedValue
     * @param <T>
     * @return
     */
    public static <T> T checkNotNull(T value, String checkedValue) {
        if(value == null) {
            throw new NullPointerException(checkedValue + " should not be null");
        }
        return value;
    }

    /**
     * 检验迭代器的每个元素不能为空
     * @param iterable
     * @param checkedIterable
     * @param <T>
     * @return
     */
    public static <T extends Iterable> T checkItemsNotNull(T iterable, String checkedIterable) {
        checkNotNull(iterable, checkedIterable);
        for (Object item : iterable) {
            checkNotNull(item, "item in " + checkedIterable);
        }
        return iterable;
    }
}
