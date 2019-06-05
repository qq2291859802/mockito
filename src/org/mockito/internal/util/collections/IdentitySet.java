/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util.collections;

import java.util.LinkedList;

/**
 * 封装list
 */
@SuppressWarnings("unchecked")
public class IdentitySet {

    LinkedList list = new LinkedList();

    /**
     * contains改成内存地址比较
     * @param o
     * @return
     */
    public boolean contains(Object o) {
        for (Object existing : list) {
            // 直接直接比较内存地址
            if (existing == o) {
                return true;
            }
        }
        return false;
    }

    public void add(Object o) {
        list.add(o);
    }
}