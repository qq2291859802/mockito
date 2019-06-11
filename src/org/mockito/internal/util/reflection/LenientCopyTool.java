/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@SuppressWarnings("unchecked")
public class LenientCopyTool {

    FieldCopier fieldCopier = new FieldCopier();

    public <T> void copyToMock(T from, T mock) {
        copy(from, mock, from.getClass(), mock.getClass().getSuperclass());
    }

    public <T> void copyToRealObject(T from, T to) {
        copy(from, to, from.getClass(), to.getClass());
    }

    private <T> void copy(T from, T to, Class fromClazz, Class toClass) {
        while (fromClazz != Object.class) {
            copyValues(from, to, fromClazz);
            // 父class类型
            fromClazz = fromClazz.getSuperclass();
        }
    }

    /**
     *
     * @param from 字段所属对象
     * @param mock mock对象
     * @param classFrom 需要修改字段的class对象
     * @param <T>
     */
    private <T> void copyValues(T from, T mock, Class classFrom) {
        // 所有字段
        Field[] fields = classFrom.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            // ignore static fields
            Field field = fields[i];
            // 过滤static字段
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            AccessibilityChanger accessibilityChanger = new AccessibilityChanger();
            try {
                accessibilityChanger.enableAccess(field);
                // 拷贝属性
                fieldCopier.copyValue(from, mock, field);
            } catch (Throwable t) {
                //Ignore - be lenient - if some field cannot be copied then let's be it
            } finally {
                // 恢复访问权限
                accessibilityChanger.safelyDisableAccess(field);
            }
        }
    }
}