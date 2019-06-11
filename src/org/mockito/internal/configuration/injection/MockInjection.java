/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.internal.configuration.injection;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.internal.util.Checks.checkItemsNotNull;
import static org.mockito.internal.util.Checks.checkNotNull;
import static org.mockito.internal.util.collections.Sets.newMockSafeHashSet;

/**
 * Internal injection configuration utility.
 *
 * mock注入的工具类
 *
 * <p>
 * Allow the user of this class to configure the way the injection of mocks will happen.
 * </p>
 *
 */
public class MockInjection {

    /**
     * Create a new configuration setup for a field
     *
     *
     * @param field Field needing mock injection
     * @param ofInstance Instance owning the <code>field</code>
     * @return New configuration builder
     */
    public static OngoingMockInjection onField(Field field, Object ofInstance) {
        return new OngoingMockInjection(field, ofInstance);
    }

    /**
     * Create a new configuration setup for fields
     *
     *
     * @param fields Fields needing mock injection
     * @param ofInstance Instance owning the <code>field</code>
     * @return New configuration builder
     */
    public static OngoingMockInjection onFields(Set<Field> fields, Object ofInstance) {
        return new OngoingMockInjection(fields, ofInstance);
    }

    /**
     * Ongoing configuration of the mock injector.
     */
    public static class OngoingMockInjection {
        // 需要注入的字段列表
        private final Set<Field> fields = new HashSet<Field>();
        // 准备注入的mock对象列表(可能存在多余的)
        private final Set<Object> mocks = newMockSafeHashSet();
        // 需要注入的字段列表所属实例
        private final Object fieldOwner;
        // 前置注入策略链
        private final MockInjectionStrategy injectionStrategies = MockInjectionStrategy.nop();
        // 后置注入策略链
        private final MockInjectionStrategy postInjectionStrategies = MockInjectionStrategy.nop();

        private OngoingMockInjection(Field field, Object fieldOwner) {
            this(Collections.singleton(field), fieldOwner);
        }

        private OngoingMockInjection(Set<Field> fields, Object fieldOwner) {
            this.fieldOwner = checkNotNull(fieldOwner, "fieldOwner");
            this.fields.addAll(checkItemsNotNull(fields, "fields"));
        }

        public OngoingMockInjection withMocks(Set<Object> mocks) {
            this.mocks.addAll(checkNotNull(mocks, "mocks"));
            return this;
        }

        /**
         * 先尝试构造器的方式创建字段实例
         * @return
         */
        public OngoingMockInjection tryConstructorInjection() {
            injectionStrategies.thenTry(new ConstructorInjection());
            return this;
        }

        /**
         * 尝试使用字段直接注入的方式
         * @return
         */
        public OngoingMockInjection tryPropertyOrFieldInjection() {
            injectionStrategies.thenTry(new PropertyAndSetterInjection());
            return this;
        }

        /**
         * 添加后置策略处理
         * @return
         */
        public OngoingMockInjection handleSpyAnnotation() {
            postInjectionStrategies.thenTry(new SpyOnInjectedFieldsHandler());
            return this;
        }

        /**
         * 注入的核心方法
         */
        public void apply() {
            for (Field field : fields) {
                injectionStrategies.process(field, fieldOwner, mocks);
                // 后执行
                postInjectionStrategies.process(field, fieldOwner, mocks);
            }
        }
    }
}
