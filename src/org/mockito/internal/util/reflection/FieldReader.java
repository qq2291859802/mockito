/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util.reflection;

import org.mockito.exceptions.base.MockitoException;

import java.lang.reflect.Field;

/**
 * 读取字段的工具类
 */
public class FieldReader {
    // 字段所属对象
    final Object target;
    // 字段对象
    final Field field;
    // 访问权限的工具类
    final AccessibilityChanger changer = new AccessibilityChanger();

    public FieldReader(Object target, Field field) {
        this.target = target;
        this.field = field;
        // 设置可访问
        changer.enableAccess(field);
    }

    public boolean isNull() {
            return read() == null;
    }

    /**
     * 获取字段结果
     * @return
     */
    public Object read() {
        try {
            return field.get(target);
        } catch (Exception e) {
            throw new MockitoException("Cannot read state from field: " + field + ", on instance: " + target);
        }
    }
}
