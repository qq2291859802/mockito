package org.mockito.internal.junit;


/**
 * 检查是否存在junit
 */
class JUnitDetecter {

    private boolean hasJUnit;

    JUnitDetecter() {
        try {
            Class.forName("junit.framework.ComparisonFailure");
            hasJUnit = true;
        } catch (Throwable t) {
            hasJUnit = false;
        }
    }

    public boolean hasJUnit() {
        return hasJUnit;
    }
}
