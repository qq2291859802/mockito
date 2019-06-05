/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.stubbing.answers;

import org.mockito.exceptions.Reporter;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.Serializable;

/**
 * Returns the passed parameter identity at specified index.
 *
 *
 * 返回特殊下标位置的参数值
 *
 * <p>The <code>argumentIndex</code> represents the index in the argument array of the invocation.</p>
 * <p>If this number equals -1 then the last argument is returned.</p>
 *
 * @see org.mockito.AdditionalAnswers
 * @since 1.9.5
 */
public class ReturnsArgumentAt implements Answer<Object>, Serializable {

    private static final long serialVersionUID = -589315085166295101L;

    public static final int LAST_ARGUMENT = -1;

    private final int wantedArgumentPosition;

    /**
     * Build the identity answer to return the argument at the given position in the argument array.
     *
     * @param wantedArgumentPosition The position of the argument identity to return in the invocation.
     *                      Using <code>-1</code> indicates the last argument.
     */
    public ReturnsArgumentAt(int wantedArgumentPosition) {
        this.wantedArgumentPosition = checkWithinAllowedRange(wantedArgumentPosition);
    }

    /**
     * 获取指定参数位置的值
     * @param invocation the invocation on the mock.
     *
     * @return
     * @throws Throwable
     */
    public Object answer(InvocationOnMock invocation) throws Throwable {
        validateIndexWithinInvocationRange(invocation);
        return invocation.getArguments()[actualArgumentPosition(invocation)];
    }

    /**
     * 实际的参数位置下标
     * @param invocation
     * @return
     */
    private int actualArgumentPosition(InvocationOnMock invocation) {
        return returningLastArg() ?
                lastArgumentIndexOf(invocation) :
                argumentIndexOf(invocation);
    }

    /**
     * 期望返回的位置是不是最后的参数的位置
     * @return
     */
    private boolean returningLastArg() {
        return wantedArgumentPosition == LAST_ARGUMENT;
    }

    private int argumentIndexOf(InvocationOnMock invocation) {
        return wantedArgumentPosition;
    }

    /**
     * 参数列表的最后一个位置下标
     * @param invocation
     * @return
     */
    private int lastArgumentIndexOf(InvocationOnMock invocation) {
        return invocation.getArguments().length - 1;
    }

    /**
     * 校验参数范围
     * @param argumentPosition
     * @return
     */
    private int checkWithinAllowedRange(int argumentPosition) {
        if (argumentPosition != LAST_ARGUMENT && argumentPosition < 0) {
            new Reporter().invalidArgumentRangeAtIdentityAnswerCreationTime();
        }
        return argumentPosition;
    }

    public int wantedArgumentPosition() {
        return wantedArgumentPosition;
    }

    /**
     * 校验是不是有效的参数范围
     * @param invocation
     */
    public void validateIndexWithinInvocationRange(InvocationOnMock invocation) {
        if (!argumentPositionInRange(invocation)) {
            new Reporter().invalidArgumentPositionRangeAtInvocationTime(invocation,
                                                                        returningLastArg(),
                                                                        wantedArgumentPosition);
        }
    }

    private boolean argumentPositionInRange(InvocationOnMock invocation) {
        int actualArgumentPosition = actualArgumentPosition(invocation);
        if (actualArgumentPosition < 0) {
            return false;
        }
        if (!invocation.getMethod().isVarArgs()) {
            // 是不是可变参数的方法
            return invocation.getArguments().length > actualArgumentPosition;
        }
        // for all varargs accepts positive ranges
        return true;
    }

    /**
     * 获取参数类型(可变参数取元素类型)
     * @param invocation
     * @return
     */
    public Class returnedTypeOnSignature(InvocationOnMock invocation) {
        // 实际要获取的参数位置
        int actualArgumentPosition = actualArgumentPosition(invocation);

        if(!invocation.getMethod().isVarArgs()) {
            // 不是可变参数
            return invocation.getMethod().getParameterTypes()[actualArgumentPosition];
        }
            // -------可变参数-----
        Class<?>[] parameterTypes = invocation.getMethod().getParameterTypes();
        int varargPosition = parameterTypes.length - 1;
        // 如果实际参数位置和可变参数位置不一致，就直接返回当前位置的类型。如果就是指向的是可变参数位置，就返回其元素类型
        if(actualArgumentPosition < varargPosition) {
            return parameterTypes[actualArgumentPosition];
        } else {
            return parameterTypes[varargPosition].getComponentType();
        }
    }
}
