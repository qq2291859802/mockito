/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.stubbing;

import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.mockito.internal.invocation.InvocationMatcher;
import org.mockito.invocation.DescribedInvocation;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * 测试桩匹配器（用于匹配设置的测试值）
 */
@SuppressWarnings("unchecked")
public class StubbedInvocationMatcher extends InvocationMatcher implements Answer, Serializable {

    private static final long serialVersionUID = 4919105134123672727L;
    // 保存多个测试值
    private final Queue<Answer> answers = new ConcurrentLinkedQueue<Answer>();
    private DescribedInvocation usedAt;

    public StubbedInvocationMatcher(InvocationMatcher invocation, Answer answer) {
        super(invocation.getInvocation(), invocation.getMatchers());
        this.answers.add(answer);
    }

    /**
     * 获取测试桩设置的值
     * @param invocation the invocation on the mock.
     *
     * @return
     * @throws Throwable
     */
    public Object answer(InvocationOnMock invocation) throws Throwable {
        //see ThreadsShareGenerouslyStubbedMockTest
        Answer a;
        synchronized(answers) {
            a = answers.size() == 1 ? answers.peek() : answers.poll();
        }
        return a.answer(invocation);
    }

    public void addAnswer(Answer answer) {
        answers.add(answer);
    }

    /**
     * 标记测试桩已经被使用
     * @param usedAt
     */
    public void markStubUsed(DescribedInvocation usedAt) {
        this.usedAt = usedAt;
    }

    /**
     * 判断是否已经被使用
     * @return
     */
    public boolean wasUsed() {
        return usedAt != null;
    }

    @Override
    public String toString() {
        return super.toString() + " stubbed with: " + answers;
    }
}