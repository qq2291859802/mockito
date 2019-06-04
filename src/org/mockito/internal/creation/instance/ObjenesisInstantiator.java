package org.mockito.internal.creation.instance;

import org.mockito.internal.configuration.GlobalConfiguration;
import org.objenesis.ObjenesisStd;

/*
objenesis简介：

objenesis是一个小型Java类库用来实例化一个特定class的对象。

使用场合：

Java已经支持使用Class.newInstance()动态实例化类的实例。但是类必须拥有一个合适的构造器。有很多场景下不能使用这种方式实例化类，比如：

构造器需要参数

构造器有side effects

构造器会抛异常

因此，在类库中经常会有类必须拥有一个默认构造器的限制。Objenesis通过绕开对象实例构造器来克服这个限制。

典型使用

实例化一个对象而不调用构造器是一个特殊的任务，然而在一些特定的场合是有用的：

序列化，远程调用和持久化 -对象需要实例化并存储为到一个特殊的状态，而没有调用代码。

代理，AOP库和Mock对象 -类可以被子类继承而子类不用担心父类的构造器

容器框架 -对象可以以非标准的方式被动态实例化。

 */

class ObjenesisInstantiator implements Instantiator {

    //TODO: in order to provide decent exception message when objenesis is not found,
    //have a constructor in this class that tries to instantiate ObjenesisStd and if it fails then show decent exception that dependency is missing
    //TODO: for the same reason catch and give better feedback when hamcrest core is not found.
    private final ObjenesisStd objenesis = new ObjenesisStd(new GlobalConfiguration().enableClassCache());

    public <T> T newInstance(Class<T> cls) {
        return objenesis.newInstance(cls);
    }
}
