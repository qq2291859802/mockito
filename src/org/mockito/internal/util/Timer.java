package org.mockito.internal.util;

public class Timer {
    // 间隔时间（毫秒）
    private final long durationMillis;
    private long startTime = -1;

    public Timer(long durationMillis) {
        this.durationMillis = durationMillis;
    }

    /**
     * Informs whether the timer is still counting down.
     *
     * 是否在间隔时间范围内
     */
    public boolean isCounting() {
        assert startTime != -1;
        return System.currentTimeMillis() - startTime <= durationMillis;
    }

    /**
     * Starts the timer count down.
     *
     * 起始计时
     */
    public void start() {
        startTime = System.currentTimeMillis();
    }
}
