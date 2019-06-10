/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.stubbing;

import org.mockito.stubbing.Answer;
import org.mockito.stubbing.DeprecatedOngoingStubbing;
import org.mockito.stubbing.OngoingStubbing;

/**
 * 连续的测试桩（支持一个一个的返回结果）
 * @param <T>
 */
public class ConsecutiveStubbing<T> extends BaseStubbing<T> {

    // 用于保存多个最终的结果(通过方法addConsecutiveAnswer)
    private final InvocationContainerImpl invocationContainerImpl;

    public ConsecutiveStubbing(InvocationContainerImpl invocationContainerImpl) {
        this.invocationContainerImpl = invocationContainerImpl;
    }

    public OngoingStubbing<T> thenAnswer(Answer<?> answer) {
        invocationContainerImpl.addConsecutiveAnswer(answer);
        return this;
    }

    public OngoingStubbing<T> then(Answer<?> answer) {
        return thenAnswer(answer);
    }
    
    public DeprecatedOngoingStubbing<T> toAnswer(Answer<?> answer) {
        invocationContainerImpl.addConsecutiveAnswer(answer);
        return this;
    }

    public <M> M getMock() {
        return (M) invocationContainerImpl.invokedMock();
    }
}