package org.mockito.internal.util.collections;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * Utilities for Iterables
 */
public class Iterables {

    /**
     * 将枚举元素添加到集合中
     *
     * Converts enumeration into iterable
     */
    public static <T> Iterable<T> toIterable(Enumeration<T> in) {
        List<T> out = new LinkedList<T>();
        while(in.hasMoreElements()) {
            out.add(in.nextElement());
        }
        return out;
    }
}
