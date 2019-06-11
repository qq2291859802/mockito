/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util.reflection;

import java.lang.reflect.AccessibleObject;

/**
 * 改变字段,方法等权限
 */
public class AccessibilityChanger {
    // 用于保存对象原有的访问权限
    private Boolean wasAccessible = null;

    /**
     * 恢复原有的访问权限
     * safely disables access
     */
    public void safelyDisableAccess(AccessibleObject accessibleObject) {
        assert wasAccessible != null : "accessibility info shall not be null";
        try {
            accessibleObject.setAccessible(wasAccessible);
        } catch (Throwable t) {
            //ignore
        }
    }

    /**
     * 访问权限可用
     * changes the accessibleObject accessibility and returns true if accessibility was changed
     */
    public void enableAccess(AccessibleObject accessibleObject) {
        // 保存对象原有的访问权限
        wasAccessible = accessibleObject.isAccessible();
        // 设置可访问
        accessibleObject.setAccessible(true);
    }
}
