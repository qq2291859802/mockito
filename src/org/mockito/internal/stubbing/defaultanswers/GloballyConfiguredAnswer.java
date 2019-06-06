/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.stubbing.defaultanswers;

import java.io.Serializable;

import org.mockito.configuration.IMockitoConfiguration;
import org.mockito.internal.configuration.GlobalConfiguration;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * 通过全局的配置获取结果（可以修改配置改变值）
 *
 * Globally configured Answer.
 * <p>
 * See javadoc for {@link IMockitoConfiguration}
 */
public class GloballyConfiguredAnswer implements Answer<Object>, Serializable {
    
    private static final long serialVersionUID = 3585893470101750917L;

    public Object answer(InvocationOnMock invocation) throws Throwable {
        return new GlobalConfiguration().getDefaultAnswer().answer(invocation);
    }
}
