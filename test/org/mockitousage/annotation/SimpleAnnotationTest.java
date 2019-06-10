package org.mockitousage.annotation;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockitoutil.TestBase;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by zhangxiqiang on 2019/6/5.
 */
public class SimpleAnnotationTest extends TestBase {


    @Test
    public void testMock() throws Exception {
        //mock一个Iterator类
        Iterator iterator = mock(Iterator.class);
        //预设当iterator调用next()时第一次返回hello，第n次都返回world
        when(iterator.next()).thenReturn("hello").thenReturn("world");
        //使用mock的对象
        String result = iterator.next() + " " + iterator.next() + " " + iterator.next();
        System.out.println(result);
        //验证结果
        assertEquals("hello world world", result);
    }

    @Test(expected = IOException.class)
    public void whenThenThrow() throws IOException {
        OutputStream outputStream = mock(OutputStream.class);
        //预设当流关闭时抛出异常
        doThrow(new IOException()).when(outputStream).close();
        outputStream.close();
    }

    @Test
    public void withUnspecifiedArguments() {
        List list = Mockito.mock(List.class);
        //匹配任意参数
        Mockito.when(list.get(Mockito.anyInt())).thenReturn(1);
        System.out.println(list.get(1));

    }

    @Test
    public void answerTest() {
        List mockList = Mockito.mock(List.class);
        //使用方法预期回调接口生成期望值（Answer结构）
        Mockito.when(mockList.get(Mockito.anyInt())).thenAnswer(new CustomAnswer());
        Assert.assertEquals("hello world:0", mockList.get(0));
        Assert.assertEquals("hello world:999", mockList.get(999));
    }

    private class CustomAnswer implements Answer<String> {
        public String answer(InvocationOnMock invocation) throws Throwable {
            Object[] args = invocation.getArguments();
            return "hello world:" + args[0];
        }
    }


    @Test
    public void spySimpleDemo() {
        List<String> list = new LinkedList<String>();
        // spy修饰的是一个对象
        List<String> spy = spy(list);
        when(spy.size()).thenReturn(100);

        spy.add("one");
        spy.add("two");
/*        spy的原理是，如果不打桩默认都会执行真实的方法，如果打桩则返回桩实现。
        可以看出spy.size()通过桩实现返回了值100，而spy.get(0)则返回了实际值*/
        assertEquals(spy.get(0), "one");
        assertEquals(100, spy.size());
    }


}
