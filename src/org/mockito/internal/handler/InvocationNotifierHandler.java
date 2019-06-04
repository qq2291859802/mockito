/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.handler;

import org.mockito.exceptions.Reporter;
import org.mockito.internal.InternalMockHandler;
import org.mockito.internal.listeners.NotifiedMethodInvocationReport;
import org.mockito.internal.stubbing.InvocationContainer;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.MockHandler;
import org.mockito.listeners.InvocationListener;
import org.mockito.mock.MockCreationSettings;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.VoidMethodStubbable;

import java.util.List;

/**
 * Handler, that call all listeners wanted for this mock, before delegating it
 * to the parameterized handler.
 *
 *
 * 带有通知能力的处理器
 *
 * Also imposterize MockHandlerImpl, delegate all call of InternalMockHandler to the real mockHandler
 */
class InvocationNotifierHandler<T> implements MockHandler, InternalMockHandler<T> {

    private final List<InvocationListener> invocationListeners;
    private final InternalMockHandler<T> mockHandler;

    public InvocationNotifierHandler(InternalMockHandler<T> mockHandler, MockCreationSettings settings) {
        this.mockHandler = mockHandler;
        this.invocationListeners = settings.getInvocationListeners();
    }

    /**
     * 处理一个调用器
     * @param invocation The invocation to handle
     * @return
     * @throws Throwable
     */
    public Object handle(Invocation invocation) throws Throwable {
        try {
            // 返回值
            Object returnedValue = mockHandler.handle(invocation);

            notifyMethodCall(invocation, returnedValue);
            return returnedValue;
        } catch (Throwable t){
            notifyMethodCallException(invocation, t);
            throw t;
        }
    }

    /**
     * 通知方法回调
     * @param invocation
     * @param returnValue
     */
	private void notifyMethodCall(Invocation invocation, Object returnValue) {
	    // 通知所有的监听器
		for (InvocationListener listener : invocationListeners) {
            try {
                listener.reportInvocation(new NotifiedMethodInvocationReport(invocation, returnValue));
            } catch(Throwable listenerThrowable) {
                new Reporter().invocationListenerThrewException(listener, listenerThrowable);
            }
        }
	}

    /**
     * 通知方法回调异常
     * @param invocation
     * @param exception
     */
    private void notifyMethodCallException(Invocation invocation, Throwable exception) {
		for (InvocationListener listener : invocationListeners) {
            try {
                listener.reportInvocation(new NotifiedMethodInvocationReport(invocation, exception));
            } catch(Throwable listenerThrowable) {
                new Reporter().invocationListenerThrewException(listener, listenerThrowable);
            }
        }
	}

    public MockCreationSettings getMockSettings() {
        return mockHandler.getMockSettings();
    }

    public VoidMethodStubbable<T> voidMethodStubbable(T mock) {
        return mockHandler.voidMethodStubbable(mock);
    }

    public void setAnswersForStubbing(List<Answer> answers) {
        mockHandler.setAnswersForStubbing(answers);
    }

    public InvocationContainer getInvocationContainer() {
        return mockHandler.getInvocationContainer();
    }

}
