/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.handler;

import org.mockito.internal.InternalMockHandler;
import org.mockito.mock.MockCreationSettings;

/**
 * by Szczepan Faber, created at: 5/21/12
 *
 * mock handler创建工厂
 */
public class MockHandlerFactory {

    public InternalMockHandler create(MockCreationSettings settings) {
        // 核心处理器
        InternalMockHandler handler = new MockHandlerImpl(settings);
        // 处理null和基本数据类型(静态代理)
        InternalMockHandler nullResultGuardian = new NullResultGuardian(handler);
        // 通知监听者
        InternalMockHandler notifier = new InvocationNotifierHandler(nullResultGuardian, settings);

        return notifier;
    }
}
