/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.internal.progress;

import org.mockito.exceptions.Reporter;
import org.mockito.internal.configuration.GlobalConfiguration;
import org.mockito.internal.debugging.Localized;
import org.mockito.internal.debugging.LocationImpl;
import org.mockito.internal.listeners.MockingProgressListener;
import org.mockito.internal.listeners.MockingStartedListener;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.Location;
import org.mockito.verification.VerificationMode;

@SuppressWarnings("unchecked")
public class MockingProgressImpl implements MockingProgress {

    private final Reporter reporter = new Reporter();
    // 参数匹配
    private final ArgumentMatcherStorage argumentMatcherStorage = new ArgumentMatcherStorageImpl();
    // 用于保存iOngoingStubbing
    IOngoingStubbing iOngoingStubbing;
    // 保存校验模式
    private Localized<VerificationMode> verificationMode;
    private Location stubbingInProgress = null;
    // 进程监听器
    private MockingProgressListener listener;

    /**
     * 添加iOngoingStubbing
     *
     * @param iOngoingStubbing
     */
    public void reportOngoingStubbing(IOngoingStubbing iOngoingStubbing) {
        this.iOngoingStubbing = iOngoingStubbing;
    }

    /**
     * 获取iOngoingStubbing
     *
     * @return
     */
    public IOngoingStubbing pullOngoingStubbing() {
        IOngoingStubbing temp = iOngoingStubbing;
        iOngoingStubbing = null;
        return temp;
    }

    /**
     * 校验开始触发
     */
    public void verificationStarted(VerificationMode verify) {
        validateState();
        resetOngoingStubbing();
        // 设置校验模式
        verificationMode = new Localized(verify);
    }

    /* (non-Javadoc)
     * @see org.mockito.internal.progress.MockingProgress#resetOngoingStubbing()
     */
    public void resetOngoingStubbing() {
        iOngoingStubbing = null;
    }

    /**
     * 获取校验模式
     *
     * @return
     */
    public VerificationMode pullVerificationMode() {
        if (verificationMode == null) {
            return null;
        }

        VerificationMode temp = verificationMode.getObject();
        verificationMode = null;
        return temp;
    }

    public void stubbingStarted() {
        validateState();
        stubbingInProgress = new LocationImpl();
    }

    /**
     * 校验状态
     */
    public void validateState() {
        validateMostStuff();

        //validate stubbing:
        // 校验是不是含有未成功的测试桩
        if (stubbingInProgress != null) {
            Location temp = stubbingInProgress;
            stubbingInProgress = null;
            reporter.unfinishedStubbing(temp);
        }
    }

    /**
     * 校验
     */
    private void validateMostStuff() {
        //State is cool when GlobalConfiguration is already loaded
        //this cannot really be tested functionally because I cannot dynamically mess up org.mockito.configuration.MockitoConfiguration class
        GlobalConfiguration.validate();

        // 判断是不是存在未完成的校验
        if (verificationMode != null) {
            Location location = verificationMode.getLocation();
            verificationMode = null;
            reporter.unfinishedVerificationException(location);
        }

        // 校验参数匹配器
        getArgumentMatcherStorage().validateState();
    }

    public void stubbingCompleted(Invocation invocation) {
        stubbingInProgress = null;
    }

    public String toString() {
        return "iOngoingStubbing: " + iOngoingStubbing +
                ", verificationMode: " + verificationMode +
                ", stubbingInProgress: " + stubbingInProgress;
    }

    /**
     * 重置mock进程
     */
    public void reset() {
        stubbingInProgress = null;
        verificationMode = null;
        getArgumentMatcherStorage().reset();
    }

    public ArgumentMatcherStorage getArgumentMatcherStorage() {
        return argumentMatcherStorage;
    }


    public void mockingStarted(Object mock, Class classToMock) {
        //  检验,触发监听器
        if (listener instanceof MockingStartedListener) {
            ((MockingStartedListener) listener).mockingStarted(mock, classToMock);
        }
        validateMostStuff();
    }

    public void setListener(MockingProgressListener listener) {
        this.listener = listener;
    }
}