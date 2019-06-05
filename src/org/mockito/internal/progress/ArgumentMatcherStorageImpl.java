/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.internal.progress;

import org.hamcrest.Matcher;
import org.mockito.exceptions.Reporter;
import org.mockito.internal.matchers.And;
import org.mockito.internal.matchers.LocalizedMatcher;
import org.mockito.internal.matchers.Not;
import org.mockito.internal.matchers.Or;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * 使用堆栈的方式实现匹配器的添加,获取和重置等
 */
@SuppressWarnings("unchecked")
public class ArgumentMatcherStorageImpl implements ArgumentMatcherStorage {
    // 表示需要两个元素匹配
    public static final int TWO_SUB_MATCHERS = 2;
    // 表示需要一个元素匹配
    public static final int ONE_SUB_MATCHER = 1;
    // 保存匹配器
    private final Stack<LocalizedMatcher> matcherStack = new Stack<LocalizedMatcher>();
    
    /* (non-Javadoc)
     * @see org.mockito.internal.progress.ArgumentMatcherStorage#reportMatcher(org.hamcrest.Matcher)
     */

    /**
     * 添加匹配器
     * @param matcher
     * @return
     */
    public HandyReturnValues reportMatcher(Matcher matcher) {
        matcherStack.push(new LocalizedMatcher(matcher));
        return new HandyReturnValues();
    }

    /* (non-Javadoc)
     * @see org.mockito.internal.progress.ArgumentMatcherStorage#pullLocalizedMatchers()
     */
    public List<LocalizedMatcher> pullLocalizedMatchers() {
        if (matcherStack.isEmpty()) {
            return Collections.emptyList();
        }
        // 深度拷贝
        List<LocalizedMatcher> matchers = new ArrayList<LocalizedMatcher>(matcherStack);
        matcherStack.clear();
        return (List) matchers;
    }

    /* (non-Javadoc)
    * @see org.mockito.internal.progress.ArgumentMatcherStorage#reportAnd()
    */
    public HandyReturnValues reportAnd() {
        assertStateFor("And(?)", TWO_SUB_MATCHERS);
        And and = new And(popLastArgumentMatchers(TWO_SUB_MATCHERS));
        matcherStack.push(new LocalizedMatcher(and));
        return new HandyReturnValues();
    }

    /* (non-Javadoc)
     * @see org.mockito.internal.progress.ArgumentMatcherStorage#reportOr()
     */
    public HandyReturnValues reportOr() {
        assertStateFor("Or(?)", TWO_SUB_MATCHERS);
        Or or = new Or(popLastArgumentMatchers(TWO_SUB_MATCHERS));
        matcherStack.push(new LocalizedMatcher(or));
        return new HandyReturnValues();
    }

    /* (non-Javadoc)
     * @see org.mockito.internal.progress.ArgumentMatcherStorage#reportNot()
     */
    public HandyReturnValues reportNot() {
        assertStateFor("Not(?)", ONE_SUB_MATCHER);
        Not not = new Not(popLastArgumentMatchers(ONE_SUB_MATCHER).get(0));
        matcherStack.push(new LocalizedMatcher(not));
        return new HandyReturnValues();
    }

    /**
     * 添加和校验匹配器
     * @param additionalMatcherName 需要添加的匹配器个数
     * @param subMatchersCount 添加之后栈中匹配器个数(用于比较)
     */
    private void assertStateFor(String additionalMatcherName, int subMatchersCount) {
        assertMatchersFoundFor(additionalMatcherName);
        assertIncorrectUseOfAdditionalMatchers(additionalMatcherName, subMatchersCount);
    }

    /**
     * pop指定个数的Matcher
     * @param count
     * @return
     */
    private List<Matcher> popLastArgumentMatchers(int count) {
        List<Matcher> result = new LinkedList<Matcher>();
        result.addAll(matcherStack.subList(matcherStack.size() - count, matcherStack.size()));
        for (int i = 0; i < count; i++) {
            matcherStack.pop();
        }
        return result;
    }

    /**
     * 从matcherStack中查找匹配器
     * @param additionalMatcherName
     */
    private void assertMatchersFoundFor(String additionalMatcherName) {
        if (matcherStack.isEmpty()) {
            matcherStack.clear();
            new Reporter().reportNoSubMatchersFound(additionalMatcherName);
        }
    }

    /**
     * 校验匹配器个数
     * @param additionalMatcherName
     * @param count
     */
    private void assertIncorrectUseOfAdditionalMatchers(String additionalMatcherName, int count) {
        if(matcherStack.size() < count) {
            ArrayList<LocalizedMatcher> lastMatchers = new ArrayList<LocalizedMatcher>(matcherStack);
            matcherStack.clear();
            new Reporter().incorrectUseOfAdditionalMatchers(additionalMatcherName, count, lastMatchers);
        }
    }

    public void validateState() {
        if (!matcherStack.isEmpty()) {
            ArrayList lastMatchers = new ArrayList<LocalizedMatcher>(matcherStack);
            matcherStack.clear();
            new Reporter().misplacedArgumentMatcher(lastMatchers);
        }
    }

    public void reset() {
        matcherStack.clear();
    }
}