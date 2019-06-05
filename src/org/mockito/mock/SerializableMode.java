package org.mockito.mock;

import org.mockito.Incubating;

/**
 * Mock serializable style.
 *
 * 序列化模式
 */
@Incubating
public enum SerializableMode {

    /**
     * No serialization.
     * 不序列化
     */
    NONE,

    /**
     * Basic serializable mode for mock objects. Introduced in Mockito 1.8.1.
     *
     *
     */
    BASIC,

    /**
     * Useful if the mock is deserialized in a different classloader / vm.
     */
    @Incubating
    ACROSS_CLASSLOADERS
}