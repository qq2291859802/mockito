package org.mockitousage.annotation;

import org.junit.Test;
import org.mockito.Mock;
import org.mockitoutil.TestBase;

import java.util.List;

import static org.mockito.Mockito.verify;

/**
 * Created by zhangxiqiang on 2019/6/5.
 */
public class SimpleAnnotationTest extends TestBase {
    @Mock
    private List list;

    @Test
    public void testMock() throws Exception {
        list.add(1);
        verify(list).add(2);
    }

}
