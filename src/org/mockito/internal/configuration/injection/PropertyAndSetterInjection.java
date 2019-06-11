/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.internal.configuration.injection;

import org.mockito.exceptions.Reporter;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.internal.configuration.injection.filter.FinalMockCandidateFilter;
import org.mockito.internal.configuration.injection.filter.MockCandidateFilter;
import org.mockito.internal.configuration.injection.filter.NameBasedCandidateFilter;
import org.mockito.internal.configuration.injection.filter.TypeBasedCandidateFilter;
import org.mockito.internal.util.collections.ListUtil;
import org.mockito.internal.util.reflection.FieldInitializationReport;
import org.mockito.internal.util.reflection.FieldInitializer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.mockito.internal.util.collections.Sets.newMockSafeHashSet;

/**
 * Inject mocks using first setters then fields, if no setters available.
 *
 * <p>
 * <u>Algorithm :<br></u>
 * for each field annotated by @InjectMocks
 *   <ul>
 *   <li>initialize field annotated by @InjectMocks
 *   <li>for each fields of a class in @InjectMocks type hierarchy
 *     <ul>
 *     <li>make a copy of mock candidates
 *     <li>order fields rom sub-type to super-type, then by field name
 *     <li>for the list of fields in a class try two passes of :
 *         <ul>
 *             <li>find mock candidate by type
 *             <li>if more than <b>*one*</b> candidate find mock candidate on name
 *             <li>if one mock candidate then
 *                 <ul>
 *                     <li>set mock by property setter if possible
 *                     <li>else set mock by field injection
 *                 </ul>
 *             <li>remove mock from mocks copy (mocks are just injected once in a class)
 *             <li>remove injected field from list of class fields
 *         </ul>
 *     <li>else don't fail, user will then provide dependencies
 *     </ul>
 *   </ul>
 * </p>
 *
 * <p>
 * <u>Note:</u> If the field needing injection is not initialized, the strategy tries
 * to create one using a no-arg constructor of the field type.
 * </p>
 */
public class PropertyAndSetterInjection extends MockInjectionStrategy {
    // mock候选过滤器
    private final MockCandidateFilter mockCandidateFilter = new TypeBasedCandidateFilter(new NameBasedCandidateFilter(new FinalMockCandidateFilter()));
    private final Comparator<Field> superTypesLast = new FieldTypeAndNameComparator();

    /**
     * 不是final / static修饰的字段
     */
    private final ListUtil.Filter<Field> notFinalOrStatic = new ListUtil.Filter<Field>() {
        public boolean isOut(Field object) {
            return Modifier.isFinal(object.getModifiers()) || Modifier.isStatic(object.getModifiers());
        }
    };


    public boolean processInjection(Field injectMocksField, Object injectMocksFieldOwner, Set<Object> mockCandidates) {
        // Set<Object> mocksToBeInjected = new HashSet<Object>(mockCandidates);
        FieldInitializationReport report = initializeInjectMocksField(injectMocksField, injectMocksFieldOwner);

        // for each field in the class hierarchy
        boolean injectionOccurred = false;
        Class<?> fieldClass = report.fieldClass();
        Object fieldInstanceNeedingInjection = report.fieldInstance();
        while (fieldClass != Object.class) {
            injectionOccurred |= injectMockCandidates(fieldClass, newMockSafeHashSet(mockCandidates), fieldInstanceNeedingInjection);
            fieldClass = fieldClass.getSuperclass();
        }
        return injectionOccurred;
    }

    /**
     * 字段注入mock值
     * @param field
     * @param fieldOwner
     * @return
     */
    private FieldInitializationReport initializeInjectMocksField(Field field, Object fieldOwner) {
        FieldInitializationReport report = null;
        try {
            report = new FieldInitializer(fieldOwner, field).initialize();
        } catch (MockitoException e) {
            if(e.getCause() instanceof InvocationTargetException) {
                Throwable realCause = e.getCause().getCause();
                new Reporter().fieldInitialisationThrewException(field, realCause);
            }
            new Reporter().cannotInitializeForInjectMocksAnnotation(field.getName(), e);
        }
        return report; // never null
    }

    /**
     * 注入字段值
     * @param awaitingInjectionClazz
     * @param mocks
     * @param instance
     * @return
     */
    private boolean injectMockCandidates(Class<?> awaitingInjectionClazz, Set<Object> mocks, Object instance) {
        boolean injectionOccurred = false;
        List<Field> orderedInstanceFields = orderedInstanceFieldsFrom(awaitingInjectionClazz);
        // pass 1
        injectionOccurred |= injectMockCandidatesOnFields(mocks, instance, injectionOccurred, orderedInstanceFields);
        // pass 2
        injectionOccurred |= injectMockCandidatesOnFields(mocks, instance, injectionOccurred, orderedInstanceFields);
        return injectionOccurred;
    }

    private boolean injectMockCandidatesOnFields(Set<Object> mocks, Object instance, boolean injectionOccurred, List<Field> orderedInstanceFields) {
        for (Iterator<Field> it = orderedInstanceFields.iterator(); it.hasNext(); ) {
            Field field = it.next();
            // 注入字段
            Object injected = mockCandidateFilter.filterCandidate(mocks, field, instance).thenInject();
            if (injected != null) {
                injectionOccurred |= true;
                // 移除已经注入的
                mocks.remove(injected);
                it.remove();
            }
        }
        return injectionOccurred;
    }

    /**
     * 过滤不为final或者static的字段列表
     * @param awaitingInjectionClazz
     * @return
     */
    private List<Field> orderedInstanceFieldsFrom(Class<?> awaitingInjectionClazz) {
        List<Field> declaredFields = Arrays.asList(awaitingInjectionClazz.getDeclaredFields());
        declaredFields = ListUtil.filter(declaredFields, notFinalOrStatic);
        // 排序
        Collections.sort(declaredFields, superTypesLast);

        return declaredFields;
    }

    /**
     * 字段类型和名称比较器
     */
    static class FieldTypeAndNameComparator implements Comparator<Field> {
        public int compare(Field field1, Field field2) {
            Class<?> field1Type = field1.getType();
            Class<?> field2Type = field2.getType();

            // if same type, compares on field name
            // 类型相同比较名字
            if (field1Type == field2Type) {
                return field1.getName().compareTo(field2.getName());
            }
            if(field1Type.isAssignableFrom(field2Type)) {
                // field1Type是field2Type的父类型
                return 1;
            }
            if(field2Type.isAssignableFrom(field1Type)) {
                return -1;
            }
            return 0;
        }
    }
}
