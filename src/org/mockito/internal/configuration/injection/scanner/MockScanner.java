/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.configuration.injection.scanner;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.internal.util.MockUtil;
import org.mockito.internal.util.reflection.FieldReader;

import java.lang.reflect.Field;
import java.util.Set;

import static org.mockito.internal.util.collections.Sets.newMockSafeHashSet;

/**
 * Scan mocks, and prepare them if needed.
 *
 *
 *
 * mock/spy的扫描器
 */
public class MockScanner {
    private final MockUtil mockUtil = new MockUtil();
    // 扫描对象实例
    private final Object instance;
    // 扫描class对象
    private final Class<?> clazz;

    /**
     * Creates a MockScanner.
     *
     * @param instance The test instance
     * @param clazz    The class in the type hierarchy of this instance.
     */
    public MockScanner(Object instance, Class<?> clazz) {
        this.instance = instance;
        this.clazz = clazz;
    }

    /**
     * Add the scanned and prepared mock instance to the given collection.
     *
     * <p>
     * The preparation of mocks consists only in defining a MockName if not already set.
     * </p>
     *
     * @param mocks Set of mocks
     */
    public void addPreparedMocks(Set<Object> mocks) {
        mocks.addAll(scan());
    }

    /**
     * Scan and prepare mocks for the given <code>testClassInstance</code> and <code>clazz</code> in the type hierarchy.
     *
     * 扫描所有的spy和mock字段值
     *
     * @return A prepared set of mock
     */
    private Set<Object> scan() {
        Set<Object> mocks = newMockSafeHashSet();
        for (Field field : clazz.getDeclaredFields()) {
            // mock or spies only
            FieldReader fieldReader = new FieldReader(instance, field);

            Object mockInstance = preparedMock(fieldReader.read(), field);
            if (mockInstance != null) {
                mocks.add(mockInstance);
            }
        }
        return mocks;
    }

    /**
     * 返回spy和mock字段值
     * @param instance 字段值
     * @param field 字段对象
     * @return
     */
    private Object preparedMock(Object instance, Field field) {

        if (isAnnotatedByMockOrSpy(field)) {
            return instance;
        } else if (isMockOrSpy(instance)) {
            // 如果没有@Spy,@Mock和@MockitoAnnotations.Mock注解，但是属于spy和mock的实例
            mockUtil.maybeRedefineMockName(instance, field.getName());
            return instance;
        }
        return null;
    }

    /**
     * 判断是否含有@Spy,@Mock和@MockitoAnnotations.Mock注解
     * @param field
     * @return
     */
    private boolean isAnnotatedByMockOrSpy(Field field) {
        return null != field.getAnnotation(Spy.class)
                || null != field.getAnnotation(Mock.class)
                || null != field.getAnnotation(MockitoAnnotations.Mock.class);
    }

    private boolean isMockOrSpy(Object instance) {
        return mockUtil.isMock(instance)
                || mockUtil.isSpy(instance);
    }
}
