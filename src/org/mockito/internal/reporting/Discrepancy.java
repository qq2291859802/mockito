/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.reporting;

/**
 * 差异对象
 */
public class Discrepancy {
    // 期望长度
    private final int wantedCount;
    // 实际长度
    private final int actualCount;

    public Discrepancy(int wantedCount, int actualCount) {
        this.wantedCount = wantedCount;
        this.actualCount = actualCount;
    }

    public int getWantedCount() {
        return wantedCount;
    }
    
    public String getPluralizedWantedCount() {
        return Pluralizer.pluralize(wantedCount);
    }

    public int getActualCount() {
        return actualCount;
    }

    public String getPluralizedActualCount() {
        return Pluralizer.pluralize(actualCount);
    }
}