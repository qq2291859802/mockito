/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util.reflection;

import java.lang.reflect.Field;

/**
 * 使用字段的方式设置字段值
 */
public class FieldSetter {
    // 字段所属对象
    private final Object target;
    // 字段对象
    private final Field field;

    public FieldSetter(Object target, Field field) {
        this.target = target;
        this.field = field;
    }

    /**
     * 设置字段值
     * @param value
     */
    public void set(Object value) {
        AccessibilityChanger changer = new AccessibilityChanger();
        changer.enableAccess(field);
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Access not authorized on field '" + field + "' of object '" + target + "' with value: '" + value + "'", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Wrong argument on field '" + field + "' of object '" + target + "' with value: '" + value + "', \n" +
                    "reason : " + e.getMessage(), e);
        }
        changer.safelyDisableAccess(field);
    }
}
