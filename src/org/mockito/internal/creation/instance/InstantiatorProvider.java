package org.mockito.internal.creation.instance;

import org.mockito.mock.MockCreationSettings;

/**
 * 实例化供应商
 */
public class InstantiatorProvider {

    private final static Instantiator INSTANCE = new ObjenesisInstantiator();

    /**
     * 如果settings指定需要使用构造器，就使用ConstructorInstantiator实例化
     * @param settings
     * @return
     */
    public Instantiator getInstantiator(MockCreationSettings settings) {
        if (settings.isUsingConstructor()) {
            return new ConstructorInstantiator(settings.getOuterClassInstance());
        } else {
            return INSTANCE;
        }
    }
}