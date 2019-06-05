package org.mockitousage.annotation;

import org.junit.Test;
import org.mockito.Mock;
import org.mockitoutil.TestBase;

import java.util.List;

/**
 * Created by zhangxiqiang on 2019/6/5.
 */
public class SimpleAnnotationTest extends TestBase {
    @Mock
    private List list;

    @Test
    public void testMock() throws Exception {
        int size = list.size();
        System.out.println(size);
    }

}
