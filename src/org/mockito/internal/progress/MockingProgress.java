/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.internal.progress;

import org.mockito.MockSettings;
import org.mockito.internal.listeners.MockingProgressListener;
import org.mockito.invocation.Invocation;
import org.mockito.verification.VerificationMode;

@SuppressWarnings("unchecked")
public interface MockingProgress {
    /**
     * 添加iOngoingStubbing
     *
     * @param iOngoingStubbing
     */
    void reportOngoingStubbing(IOngoingStubbing iOngoingStubbing);

    /**
     * 获取iOngoingStubbing
     *
     * @return
     */
    IOngoingStubbing pullOngoingStubbing();

    /**
     * 校验开始触发
     *
     * @param verificationMode 校验模式对象
     */
    void verificationStarted(VerificationMode verificationMode);

    /**
     * 获取校验模式
     *
     * @return
     */
    VerificationMode pullVerificationMode();

    /**
     * 测试桩开始标记
     */
    void stubbingStarted();

    /**
     * 测试桩已经完成
     *
     * @param invocation
     */
    void stubbingCompleted(Invocation invocation);

    /**
     * 校验状态
     */
    void validateState();

    /**
     * 重置mock进程
     */
    void reset();

    /**
     * 重置resetOngoingStubbing
     * Removes ongoing stubbing so that in case the framework is misused
     * state validation errors are more accurate
     */
    void resetOngoingStubbing();

    /**
     * 获取参数匹配器
     *
     * @return
     */
    ArgumentMatcherStorage getArgumentMatcherStorage();

    /**
     * mock开始执行
     *
     * @param mock
     * @param classToMock
     */
    void mockingStarted(Object mock, Class classToMock);

    /**
     * 设置进程监听器
     *
     * @param listener
     */
    void setListener(MockingProgressListener listener);
}