package features2.test0;

import features2.model.UserDo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.serialization.snack3.SnackRenderFactory;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.annotation.TestPropertySource;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2023/1/16 created
 */
@TestPropertySource("classpath:features2_test0.yml")
@RunWith(SolonJUnit4ClassRunner.class)
public class TestQuickConfig {
    @Test
    public void hello2() throws Throwable{
        UserDo userDo = new UserDo();

        Map<String, Object> data = new HashMap<>();
        data.put("time", new Date(1673861993477L));
        data.put("long", 12L);
        data.put("int", 12);
        data.put("null", null);

        userDo.setMap1(data);

        ContextEmpty ctx = new ContextEmpty();
        SnackRenderFactory.global.create().render(userDo, ctx);
        String output = ctx.attr("output");

        System.out.println(output);

        //error: map/null value 还是会输出
        assert "{\"s1\":\"noear\",\"b1\":true,\"n1\":1,\"d1\":1.0,\"map1\":{\"null\":null,\"time\":1673861993477,\"long\":12,\"int\":12}}".equals(output);
    }
}