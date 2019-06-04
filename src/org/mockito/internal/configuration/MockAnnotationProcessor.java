/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.configuration;

import org.mockito.Mock;
import org.mockito.MockSettings;
import org.mockito.Mockito;

import java.lang.reflect.Field;

/**
 * Instantiates a mock on a field annotated by {@link Mock}
 *
 * 处理@Mock注解
 */
public class MockAnnotationProcessor implements FieldAnnotationProcessor<Mock> {
    public Object process(Mock annotation, Field field) {
        MockSettings mockSettings = Mockito.withSettings();
        if (annotation.extraInterfaces().length > 0) { // never null
            mockSettings.extraInterfaces(annotation.extraInterfaces());
        }
        if ("".equals(annotation.name())) {
            // 如果没有设置mock的name字段，直接取字段名
            mockSettings.name(field.getName());
        } else {
            mockSettings.name(annotation.name());
        }
        if(annotation.serializable()){
        	mockSettings.serializable();
        }

        // see @Mock answer default value
        // 设置默认结果
        mockSettings.defaultAnswer(annotation.answer().get());
        return Mockito.mock(field.getType(), mockSettings);
    }
}
