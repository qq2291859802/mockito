package org.mockito.internal.creation.instance;

/**
 * Provides instances of classes.
 *
 *
 *
 */
public interface Instantiator {

    /**
     * 创建class对应的实例
     * Creates instance of given class
     */
    <T> T newInstance(Class<T> cls) throws InstantationException;

}
