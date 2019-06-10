/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.reporting;


import org.mockito.internal.invocation.InvocationMatcher;
import org.mockito.invocation.Invocation;

/**
 * Makes sure both wanted and actual are printed consistently (single line or multiline)
 * <p>
 * Makes arguments printed with types if necessary
 *
 * 打印期望和实际的信息
 */
public class SmartPrinter {

    private final String wanted;
    private final String actual;

    /**
     * 打印期望和实际的信息（用于问题分析）
     * @param wanted
     * @param actual
     * @param indexesOfMatchersToBeDescribedWithExtraTypeInfo 需要描述额外的类型信息的匹配器下标
     */
    public SmartPrinter(InvocationMatcher wanted, Invocation actual, Integer ... indexesOfMatchersToBeDescribedWithExtraTypeInfo) {
        PrintSettings printSettings = new PrintSettings();
        printSettings.setMultiline(wanted.toString().contains("\n") || actual.toString().contains("\n"));
        printSettings.setMatchersToBeDescribedWithExtraTypeInfo(indexesOfMatchersToBeDescribedWithExtraTypeInfo);
        // 打印想要的信息
        this.wanted = printSettings.print(wanted);
        // 打印出错的信息
        this.actual = printSettings.print(actual);
    }

    public String getWanted() {
        return wanted;
    }

    public String getActual() {
        return actual;
    }
}