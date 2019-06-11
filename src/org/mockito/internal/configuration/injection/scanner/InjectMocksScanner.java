/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.configuration.injection.scanner;

import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.exceptions.Reporter;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * Scan field for injection.
 *
 * 注解@InjectMock扫描器
 */
@SuppressWarnings("deprecation")
public class InjectMocksScanner {
    // 扫描类
    private final Class<?> clazz;

    /**
     * Create a new InjectMocksScanner for the given clazz on the given instance
     *
     * @param clazz    Current class in the hierarchy of the test
     */
    public InjectMocksScanner(Class<?> clazz) {
        this.clazz = clazz;
    }


    /**
     * Add the fields annotated by @{@link InjectMocks}
     *
     * @param mockDependentFields Set of fields annotated by  @{@link InjectMocks}
     */
    public void addTo(Set<Field> mockDependentFields) {
        mockDependentFields.addAll(scan());
    }

    /**
     * 扫描含有@InjectMocks注解的字段，返回字段列表
     *
     * Scan fields annotated by &#064;InjectMocks
     *
     * @return Fields that depends on Mock
     */
    private Set<Field> scan() {
        Set<Field> mockDependentFields = new HashSet<Field>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (null != field.getAnnotation(InjectMocks.class)) {
                // 含有字段InjectMocks
                assertNoAnnotations(field, Mock.class, MockitoAnnotations.Mock.class, Captor.class);
                mockDependentFields.add(field);
            }
        }

        return mockDependentFields;
    }

    /**
     *  如果字段存在注解列表中的某一个注解将抛出异常
     * @param field  字段
     * @param annotations 注解列表
     */
    void assertNoAnnotations(final Field field, final Class... annotations) {
        for (Class annotation : annotations) {
            if (field.isAnnotationPresent(annotation)) {
                new Reporter().unsupportedCombinationOfAnnotations(annotation.getSimpleName(), InjectMocks.class.getSimpleName());
            }
        }
    }
}
