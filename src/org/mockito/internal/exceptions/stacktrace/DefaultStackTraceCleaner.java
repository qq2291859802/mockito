package org.mockito.internal.exceptions.stacktrace;

import org.mockito.exceptions.stacktrace.StackTraceCleaner;

/**
 * 默认的堆栈日志清除器
* by Szczepan Faber, created at: 7/29/12
*/
public class DefaultStackTraceCleaner implements StackTraceCleaner {

    /**
     * 根据堆栈日志类名，判断是否排除在外
     * @param e
     * @return
     */
    public boolean isOut(StackTraceElement e) {
        boolean fromMockObject = e.getClassName().contains("$$EnhancerByMockitoWithCGLIB$$");
        boolean fromOrgMockito = e.getClassName().startsWith("org.mockito.");
        boolean isRunner = e.getClassName().startsWith("org.mockito.runners.");
        boolean isInternalRunner = e.getClassName().startsWith("org.mockito.internal.runners.");
        return (fromMockObject || fromOrgMockito) && !isRunner && !isInternalRunner;
    }
}
