/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.invocation;

import org.hamcrest.Matcher;
import org.mockito.internal.matchers.ArrayEquals;
import org.mockito.internal.matchers.Equals;
import org.mockito.internal.util.collections.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * by Szczepan Faber, created at: 3/31/12
 */
public class ArgumentsProcessor {
    /**
     * 将含有可变参数的参数列表，转换为object[]形式
     * 如：
     * methodA(String a,Integer b, String...c)  -> methodA(Object[])
     * @param isVarArgs
     * @param args
     * @return
     */
    // expands array varArgs that are given by runtime (1, [a, b]) into true
    // varArgs (1, a, b);
    public static Object[] expandVarArgs(final boolean isVarArgs, final Object[] args) {
        if (!isVarArgs || new ArrayUtils().isEmpty(args) || args[args.length - 1] != null && !args[args.length - 1].getClass().isArray()) {
            return args == null ? new Object[0] : args;
        }
        // 如果存在可变参数，只能是最后一位是可变参数对象
        final int nonVarArgsCount = args.length - 1;
        // 可变参数对象
        Object[] varArgs;
        if (args[nonVarArgsCount] == null) {
            // in case someone deliberately passed null varArg array
            varArgs = new Object[] { null };
        } else {
            // 将可变参数对象转换为object[]
            varArgs = ArrayEquals.createObjectArray(args[nonVarArgsCount]);
        }
        final int varArgsCount = varArgs.length;
        // 总长度： 非可变参数长度 + 可变参数数组长度
        Object[] newArgs = new Object[nonVarArgsCount + varArgsCount];
        // 合并参数
        System.arraycopy(args, 0, newArgs, 0, nonVarArgsCount);
        System.arraycopy(varArgs, 0, newArgs, nonVarArgsCount, varArgsCount);
        return newArgs;
    }

    /**
     * 根据参数类型转换为匹配器列表
     * @param arguments
     * @return
     */
    public static List<Matcher> argumentsToMatchers(Object[] arguments) {
        List<Matcher> matchers = new ArrayList<Matcher>(arguments.length);
        for (Object arg : arguments) {
            if (arg != null && arg.getClass().isArray()) {
                // 如果是数组，使用ArrayEquals
                matchers.add(new ArrayEquals(arg));
            } else {
                // 其他使用Equals
                matchers.add(new Equals(arg));
            }
        }
        return matchers;
    }
}
