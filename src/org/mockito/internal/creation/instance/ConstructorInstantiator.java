package org.mockito.internal.creation.instance;

import java.lang.reflect.Constructor;

/**
 * 构造器实例化对象
 */
public class ConstructorInstantiator implements Instantiator {

    // 构造器参数(如果参数为null,表示空参)
    private final Object outerClassInstance;

    public ConstructorInstantiator(Object outerClassInstance) {
        this.outerClassInstance = outerClassInstance;
    }

    public <T> T newInstance(Class<T> cls) {
        if (outerClassInstance == null) {
            return noArgConstructor(cls);
        }
        return withParams(cls, outerClassInstance);
    }

    /**
     * 使用带参构造器创建对象
     * @param cls
     * @param params
     * @param <T>
     * @return
     */
    private static <T> T withParams(Class<T> cls, Object... params) {
        try {
            //this is kind of overengineered because we don't need to support more params
            //however, I know we will be needing it :)
            for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
                Class<?>[] types = constructor.getParameterTypes();
                if (paramsMatch(types, params)) {
                    return (T) constructor.newInstance(params);
                }
            }
        } catch (Exception e) {
            throw paramsException(cls, e);
        }
        throw paramsException(cls, null);
    }

    private static <T> InstantationException paramsException(Class<T> cls, Exception e) {
        return new InstantationException("Unable to create instance of '"
                + cls.getSimpleName() + "'.\nPlease ensure that the outer instance has correct type and that the target class has 0-arg constructor.", e);
    }

    /**
     * 参数匹配，如果全部匹配则返回true
     * @param types
     * @param params
     * @return
     */
    private static boolean paramsMatch(Class<?>[] types, Object[] params) {
        if (params.length != types.length) {
            return false;
        }
        for (int i = 0; i < params.length; i++) {
            if (!types[i].isInstance(params[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 使用空参构造器
     * @param cls
     * @param <T>
     * @return
     */
    private static <T> T noArgConstructor(Class<T> cls) {
        try {
            return cls.newInstance();
        } catch (Throwable t) {
            throw new InstantationException("Unable to create instance of '"
                    + cls.getSimpleName() + "'.\nPlease ensure it has 0-arg constructor which invokes cleanly.", t);
        }
    }
}
