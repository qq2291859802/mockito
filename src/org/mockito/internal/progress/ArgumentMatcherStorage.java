/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.progress;

import org.hamcrest.Matcher;
import org.mockito.internal.matchers.LocalizedMatcher;

import java.util.List;

/**
 * 参数匹配存储工具
 */
@SuppressWarnings("unchecked")
public interface ArgumentMatcherStorage {
    /**
     * 添加匹配器
     * @param matcher 匹配器对象
     * @return
     */
    HandyReturnValues reportMatcher(Matcher matcher);

    /**
     * 获取所有的匹配器
     * @return
     */
    List<LocalizedMatcher> pullLocalizedMatchers();

    /**
     * 添加一个and匹配器
     * @return
     */
    HandyReturnValues reportAnd();

    /**
     * 添加一个not匹配器
     * @return
     */
    HandyReturnValues reportNot();
    /**
     * 添加一个not匹配器
     * @return
     */
    HandyReturnValues reportOr();

    /**
     * 校验匹配器的状态
     */
    void validateState();

    /**
     * 重置匹配器
     */
    void reset();

}