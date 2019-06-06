/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito;

/**
 * 打印debugger信息接口
 */
public interface MockitoDebugger {

    //Prints all interactions with mock. Also prints stubbing information.
    //You can put it in your 'tearDown' method

    /**
     * 打印mock列表多个invocation信息
     * @param mocks
     * @return
     */
    String printInvocations(Object ... mocks);

}