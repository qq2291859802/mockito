/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.internal.util.collections;

import java.util.Collection;
import java.util.LinkedList;

/**
 * list工具类
 */
public class ListUtil {

    /**
     * 过滤掉集合中不匹配的元素
     * @param collection
     * @param filter
     * @param <T>
     * @return 需要的元素集合
     */
    public static <T> LinkedList<T> filter(Collection<T> collection, Filter<T> filter) {
        LinkedList<T> filtered = new LinkedList<T>();
        for (T t : collection) {
            if (!filter.isOut(t)) {
                filtered.add(t);
            }
        }
        return filtered;
    }

    /**
     * 过滤器
     * @param <T>
     */
    public interface Filter<T> {
        /**
         * 排除在外的对象
         * @param object
         * @return
         */
        boolean isOut(T object);
    }
}
