/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.internal.verification.checkers;

import java.util.List;

import org.mockito.exceptions.Reporter;
import org.mockito.internal.invocation.InvocationMatcher;
import org.mockito.internal.invocation.InvocationMarker;
import org.mockito.internal.invocation.InvocationsFinder;
import org.mockito.internal.reporting.Discrepancy;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.Location;

/**
 * 校验多个invocation
 */
public class NumberOfInvocationsChecker {
    
    private final Reporter reporter;
    private final InvocationsFinder finder;
    private final InvocationMarker invocationMarker = new InvocationMarker();

    public NumberOfInvocationsChecker() {
        this(new Reporter(), new InvocationsFinder());
    }
    
    NumberOfInvocationsChecker(Reporter reporter, InvocationsFinder finder) {
        this.reporter = reporter;
        this.finder = finder;
    }
    
    public void check(List<Invocation> invocations, InvocationMatcher wanted, int wantedCount) {
        List<Invocation> actualInvocations = finder.findInvocations(invocations, wanted);
        
        int actualCount = actualInvocations.size();
        if (wantedCount > actualCount) {
            Location lastInvocation = finder.getLastLocation(actualInvocations);
            // 太少
            reporter.tooLittleActualInvocations(new Discrepancy(wantedCount, actualCount), wanted, lastInvocation);
        } else if (wantedCount == 0 && actualCount > 0) {
            Location firstUndesired = actualInvocations.get(wantedCount).getLocation();
            // 期望没有实际存在
            reporter.neverWantedButInvoked(wanted, firstUndesired); 
        } else if (wantedCount < actualCount) {
            // 太多
            Location firstUndesired = actualInvocations.get(wantedCount).getLocation();
            reporter.tooManyActualInvocations(wantedCount, actualCount, wanted, firstUndesired);
        }
        // 标记已经校验完成
        invocationMarker.markVerified(actualInvocations, wanted);
    }
}