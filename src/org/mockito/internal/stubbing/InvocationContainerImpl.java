/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.stubbing;

import org.mockito.internal.invocation.InvocationMatcher;
import org.mockito.internal.invocation.StubInfoImpl;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.stubbing.answers.AnswersValidator;
import org.mockito.internal.verification.DefaultRegisteredInvocations;
import org.mockito.internal.verification.RegisteredInvocations;
import org.mockito.internal.verification.SingleRegisteredInvocation;
import org.mockito.invocation.Invocation;
import org.mockito.mock.MockCreationSettings;
import org.mockito.stubbing.Answer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Invocation容器
 */
@SuppressWarnings("unchecked")
public class InvocationContainerImpl implements InvocationContainer, Serializable {

    private static final long serialVersionUID = -5334301962749537177L;
    // 测试桩匹配器列表
    private final LinkedList<StubbedInvocationMatcher> stubbed = new LinkedList<StubbedInvocationMatcher>();
    // mock进程
    private final MockingProgress mockingProgress;
    // 设置的测试值列表
    private final List<Answer> answersForStubbing = new ArrayList<Answer>();
    // Invocation管理
    private final RegisteredInvocations registeredInvocations;
    // invocation对象的测试桩
    private InvocationMatcher invocationForStubbing;

    public InvocationContainerImpl(MockingProgress mockingProgress, MockCreationSettings mockSettings) {
        this.mockingProgress = mockingProgress;
        this.registeredInvocations = createRegisteredInvocations(mockSettings);
    }

    /**
     * 设置测试桩
     * @param invocation
     */
    public void setInvocationForPotentialStubbing(InvocationMatcher invocation) {
        registeredInvocations.add(invocation.getInvocation());
        this.invocationForStubbing = invocation;
    }

    /**
     * 重置invocation测试桩
     * @param invocationMatcher
     */
    public void resetInvocationForPotentialStubbing(InvocationMatcher invocationMatcher) {
        this.invocationForStubbing = invocationMatcher;
    }

    public void addAnswer(Answer answer) {
        registeredInvocations.removeLast();
        addAnswer(answer, false);
    }

    /**
     * 添加连续的结果
     * @param answer
     */
    public void addConsecutiveAnswer(Answer answer) {
        addAnswer(answer, true);
    }

    /**
     * 添加测试桩结果
     * @param answer
     * @param isConsecutive
     */
    public void addAnswer(Answer answer, boolean isConsecutive) {
        Invocation invocation = invocationForStubbing.getInvocation();
        mockingProgress.stubbingCompleted(invocation);
        AnswersValidator answersValidator = new AnswersValidator();
        // 校验结果
        answersValidator.validate(answer, invocation);

        synchronized (stubbed) {
            if (isConsecutive) {
                // 如果是连续的结果
                stubbed.getFirst().addAnswer(answer);
            } else {
                stubbed.addFirst(new StubbedInvocationMatcher(invocationForStubbing, answer));
            }
        }
    }

    Object answerTo(Invocation invocation) throws Throwable {
        return findAnswerFor(invocation).answer(invocation);
    }

    /**
     * 查找invocation的测试桩匹配信息
     * @param invocation
     * @return
     */
    public StubbedInvocationMatcher findAnswerFor(Invocation invocation) {
        synchronized (stubbed) {
            for (StubbedInvocationMatcher s : stubbed) {
                if (s.matches(invocation)) {
                    // 标记当前测试桩已经被使用
                    s.markStubUsed(invocation);
                    // 标记invocation执行的测试桩信息
                    invocation.markStubbed(new StubInfoImpl(s));
                    return s;
                }
            }
        }

        return null;
    }

    public void addAnswerForVoidMethod(Answer answer) {
        answersForStubbing.add(answer);
    }

    public void setAnswersForStubbing(List<Answer> answers) {
        answersForStubbing.addAll(answers);
    }

    public boolean hasAnswersForStubbing() {
        return !answersForStubbing.isEmpty();
    }

    public boolean hasInvocationForPotentialStubbing() {
        return !registeredInvocations.isEmpty();
    }

    /**
     * 设置某个方法测试桩
     * @param invocation
     */
    public void setMethodForStubbing(InvocationMatcher invocation) {
        invocationForStubbing = invocation;
        assert hasAnswersForStubbing();
        for (int i = 0; i < answersForStubbing.size(); i++) {
            addAnswer(answersForStubbing.get(i), i != 0);
        }
        answersForStubbing.clear();
    }

    @Override
    public String toString() {
        return "invocationForStubbing: " + invocationForStubbing;
    }

    public List<Invocation> getInvocations() {
        return registeredInvocations.getAll();
    }

    public List<StubbedInvocationMatcher> getStubbedInvocations() {
        return stubbed;
    }

    public Object invokedMock() {
        return invocationForStubbing.getInvocation().getMock();
    }
    
    public InvocationMatcher getInvocationForStubbing() {
    	return invocationForStubbing;
    }

    private RegisteredInvocations createRegisteredInvocations(MockCreationSettings mockSettings) {
        return mockSettings.isStubOnly()
          ? new SingleRegisteredInvocation()
          : new DefaultRegisteredInvocations();
    }
}
