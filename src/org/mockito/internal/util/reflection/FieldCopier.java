/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util.reflection;

import java.lang.reflect.Field;

/**
 * 拷贝字段工具类
 */
public class FieldCopier {
    /**
     *
     * @param from 字段所属对象
     * @param to 需要设置的字段值
     * @param field 字段对象
     * @param <T>
     * @throws IllegalAccessException
     */
    public <T> void copyValue(T from, T to, Field field) throws IllegalAccessException {
        Object value = field.get(from);
        field.set(to, value);
    }
}
