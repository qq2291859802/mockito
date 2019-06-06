/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.debugging;

import org.mockito.MockitoDebugger;
import org.mockito.internal.invocation.UnusedStubsFinder;
import org.mockito.internal.invocation.finder.AllInvocationsFinder;
import org.mockito.invocation.Invocation;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * debugger打印实现
 */
public class MockitoDebuggerImpl implements MockitoDebugger {
    // 查看Invocation
    private final AllInvocationsFinder allInvocationsFinder = new AllInvocationsFinder();

    private final UnusedStubsFinder unusedStubsFinder = new UnusedStubsFinder();

    /**
     * 打印所有的invocation和未使用的stub列表
     * @param mocks
     * @return
     */
    public String printInvocations(Object ... mocks) {
        String out = "";
        List<Invocation> invocations = allInvocationsFinder.find(asList(mocks));
        out += line("********************************");
        out += line("*** Mockito interactions log ***");
        out += line("********************************");
        for(Invocation i:invocations) {
            out += line(i.toString());
            out += line(" invoked: " + i.getLocation());
            if (i.stubInfo() != null) {
                out += line(" stubbed: " + i.stubInfo().stubbedAt().toString());
            }
        }

        invocations = unusedStubsFinder.find(asList(mocks));
        if (invocations.isEmpty()) {
            return print(out);
        }
        out += line("********************************");
        out += line("***       Unused stubs       ***");
        out += line("********************************");
        
        for(Invocation i:invocations) {
            out += line(i.toString());
            out += line(" stubbed: " + i.getLocation());
        }
        return print(out);
    }

    private String line(String text) {
        return text + "\n";
    }

    private String print(String out) {
        System.out.println(out);
        return out;
    }
}