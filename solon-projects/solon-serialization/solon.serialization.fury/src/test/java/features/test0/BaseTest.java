package features.test0;

import features.model.UserDo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.serialization.fury.FuryBytesSerializer;
import org.noear.solon.serialization.fury.FuryRender;
import org.noear.solon.test.SolonJUnit5Extension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2024/5/30 created
 */
@ExtendWith(SolonJUnit5Extension.class)
public class BaseTest {
    @Test
    public void hello2() throws Throwable{
        UserDo userDo = new UserDo();

        Map<String, Object> data = new HashMap<>();
        data.put("time", new Date(1673861993477L));
        data.put("long", 12L);
        data.put("int", 12);
        data.put("null", null);

        userDo.setMap1(data);

        ContextEmpty ctx = new ContextEmpty(){
            private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            @Override
            public OutputStream outputStream() {
                return outputStream;
            }

            @Override
            public void output(byte[] bytes) {
                try {
                    outputStream().write(bytes);
                }catch (IOException e){
                    throw new RuntimeException(e);
                }
            }

            @Override
            public byte[] bodyAsBytes() throws IOException {
                return outputStream.toByteArray();
            }
        };

        FuryRender render = new FuryRender();
        render.render(userDo, ctx);

        FuryBytesSerializer serializer = new FuryBytesSerializer();
        UserDo userDo2 = (UserDo)serializer.deserializeFromBody(ctx);

        System.out.println(userDo2);

        assert userDo.getB0() == userDo2.getB0();
        assert userDo.getS1().equals(userDo2.getS1());
        assert userDo.getMap1().size() == userDo2.getMap1().size();
    }
}
