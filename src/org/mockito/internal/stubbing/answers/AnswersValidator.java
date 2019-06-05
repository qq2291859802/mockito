/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.stubbing.answers;

import org.mockito.exceptions.Reporter;
import org.mockito.invocation.Invocation;
import org.mockito.stubbing.Answer;

/**
 * 结果校验器
 */
public class AnswersValidator {

    private final Reporter reporter = new Reporter();

    /**
     * 校验不同的Answer
     * @param answer
     * @param invocation
     */
    public void validate(Answer<?> answer, Invocation invocation) {
        MethodInfo methodInfo = new MethodInfo(invocation);

        if (answer instanceof ThrowsException) {
            validateException((ThrowsException) answer, methodInfo);
        }
        
        if (answer instanceof Returns) {
            validateReturnValue((Returns) answer, methodInfo);
        }
        
        if (answer instanceof DoesNothing) {
            validateDoNothing((DoesNothing) answer, methodInfo);
        }
        
        if (answer instanceof CallsRealMethods) {
            validateMockingConcreteClass((CallsRealMethods) answer, methodInfo);
        }

        if (answer instanceof ReturnsArgumentAt) {
            ReturnsArgumentAt returnsArgumentAt = (ReturnsArgumentAt) answer;
            validateReturnArgIdentity(returnsArgumentAt, invocation);
        }
    }

    /**
     *
     * @param returnsArgumentAt
     * @param invocation
     */
    private void validateReturnArgIdentity(ReturnsArgumentAt returnsArgumentAt, Invocation invocation) {
        returnsArgumentAt.validateIndexWithinInvocationRange(invocation);

        MethodInfo methodInfo = new MethodInfo(invocation);
        if (!methodInfo.isValidReturnType(returnsArgumentAt.returnedTypeOnSignature(invocation))) {
            new Reporter().wrongTypeOfArgumentToReturn(invocation, methodInfo.printMethodReturnType(),
                                                       returnsArgumentAt.returnedTypeOnSignature(invocation),
                                                       returnsArgumentAt.wantedArgumentPosition());
        }

    }

    /**
     * 校验是不是具体方法
     * @param answer
     * @param methodInfo
     */
    private void validateMockingConcreteClass(CallsRealMethods answer, MethodInfo methodInfo) {
        if (methodInfo.isAbstract()) {
            reporter.cannotCallAbstractRealMethod();
        }
    }

    /**
     * 校验方法是不是返回值为void
     * @param answer
     * @param methodInfo
     */
    private void validateDoNothing(DoesNothing answer, MethodInfo methodInfo) {
        if (!methodInfo.isVoid()) {
            reporter.onlyVoidMethodsCanBeSetToDoNothing();
        }
    }

    /**
     * 校验返回值
     * @param answer
     * @param methodInfo
     */
    private void validateReturnValue(Returns answer, MethodInfo methodInfo) {
        if (methodInfo.isVoid()) {
            reporter.cannotStubVoidMethodWithAReturnValue(methodInfo.getMethodName());
        }

        if (answer.returnsNull() && methodInfo.returnsPrimitive()) {
            // null 或者 基本数据类型
            reporter.wrongTypeOfReturnValue(methodInfo.printMethodReturnType(), "null", methodInfo.getMethodName());
        } 

        if (!answer.returnsNull() && !methodInfo.isValidReturnType(answer.getReturnType())) {
            reporter.wrongTypeOfReturnValue(methodInfo.printMethodReturnType(), answer.printReturnType(), methodInfo.getMethodName());
        }
    }

    /**
     * 校验异常
     * @param answer
     * @param methodInfo
     */
    private void validateException(ThrowsException answer, MethodInfo methodInfo) {
        Throwable throwable = answer.getThrowable();
        if (throwable == null) {
            reporter.cannotStubWithNullThrowable();
        }
        
        if (throwable instanceof RuntimeException || throwable instanceof Error) {
            return;
        }
        
        if (!methodInfo.isValidException(throwable)) {
            reporter.checkedExceptionInvalid(throwable);
        }
    }
}