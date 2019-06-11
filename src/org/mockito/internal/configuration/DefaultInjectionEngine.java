/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.configuration;

import org.mockito.internal.configuration.injection.MockInjection;

import java.lang.reflect.Field;
import java.util.Set;

/**
 *
 * 字段注入引擎
 *
 * Inject mock/spies dependencies for fields annotated with &#064;InjectMocks
 * <p/>
 * See {@link org.mockito.MockitoAnnotations}
 */
public class DefaultInjectionEngine {

    /**
     * 执行字段注入
     * @param needingInjection 需要注入的字段
     * @param mocks 准备注入的mock对象列表(可能存在多余的)
     * @param testClassInstance 字段所属实例
     */
    public void injectMocksOnFields(Set<Field> needingInjection, Set<Object> mocks, Object testClassInstance) {

        MockInjection.onFields(needingInjection, testClassInstance)
                .withMocks(mocks)
                .tryConstructorInjection()
                .tryPropertyOrFieldInjection()
                .handleSpyAnnotation()
                .apply();
    }

}
