/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.internal.configuration.injection;

import org.mockito.exceptions.Reporter;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.internal.util.reflection.FieldInitializationReport;
import org.mockito.internal.util.reflection.FieldInitializer;
import org.mockito.internal.util.reflection.FieldInitializer.ConstructorArgumentResolver;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 *
 * 使用构造器的方式实例化对象
 * Injection strategy based on constructor.
 *
 * <p>
 * The strategy will search for the constructor with most parameters
 * and try to resolve mocks by type.
 * </p>
 *
 * <blockquote>
 * TODO on missing mock type, shall it abandon or create "noname" mocks.
 * TODO and what if the arg type is not mockable.
 * </blockquote>
 *
 * <p>
 * For now the algorithm tries to create anonymous mocks if an argument type is missing.
 * If not possible the algorithm abandon resolution.
 * </p>
 */
public class ConstructorInjection extends MockInjectionStrategy {

    private ConstructorArgumentResolver argResolver;

    public ConstructorInjection() { }

    // visible for testing
    ConstructorInjection(ConstructorArgumentResolver argResolver) {
        this.argResolver = argResolver;
    }

    /**
     * 根据mock对象列表实例化对应的字段
     * @param field Field needing injection 需要注入的字段
     * @param fieldOwner Field owner instance. 字段所属实例
     * @param mockCandidates Pool of mocks to inject. 准备注入的mock对象列表
     * @return 字段是否使用构造器参数实例化对象
     */
    public boolean processInjection(Field field, Object fieldOwner, Set<Object> mockCandidates) {
        try {
            SimpleArgumentResolver simpleArgumentResolver = new SimpleArgumentResolver(mockCandidates);
            FieldInitializationReport report = new FieldInitializer(fieldOwner, field, simpleArgumentResolver).initialize();

            return report.fieldWasInitializedUsingContructorArgs();
        } catch (MockitoException e) {
            if(e.getCause() instanceof InvocationTargetException) {
                Throwable realCause = e.getCause().getCause();
                new Reporter().fieldInitialisationThrewException(field, realCause);
            }
            // other causes should be fine
            return false;
        }

    }

    /**
     * Returns mocks that match the argument type, if not possible assigns null.
     */
    static class SimpleArgumentResolver implements ConstructorArgumentResolver {
        // 准备匹配构造器参数类型的实例对象列表
        final Set<Object> objects;

        public SimpleArgumentResolver(Set<Object> objects) {
            this.objects = objects;
        }

        /**
         *
         * @param argTypes Constructor argument types, should not be null. 构造器的参数类型列表
         * @return
         */
        public Object[] resolveTypeInstances(Class<?>... argTypes) {
            List<Object> argumentInstances = new ArrayList<Object>(argTypes.length);
            for (Class<?> argType : argTypes) {
                argumentInstances.add(objectThatIsAssignableFrom(argType));
            }
            return argumentInstances.toArray();
        }

        /**
         * 如果对象object其类型和argType类型匹配，就返回object;否则返回null
         * @param argType
         * @return
         */
        private Object objectThatIsAssignableFrom(Class<?> argType) {
            for (Object object : objects) {
                if(argType.isAssignableFrom(object.getClass())) return object;
            }
            return null;
        }
    }

}
