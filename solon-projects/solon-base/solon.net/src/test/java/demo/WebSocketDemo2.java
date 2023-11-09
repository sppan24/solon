package demo;

import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.websocket.Listener.PipelineWebSocketListener;
import org.noear.solon.net.websocket.Listener.SimpleWebSocketListener;
import org.noear.solon.net.websocket.WebSocket;

import java.io.IOException;

/**
 * @author noear
 * @since 2.6
 */
@ServerEndpoint("/user/{userId}")
public class WebSocketDemo2 extends PipelineWebSocketListener {
    public WebSocketDemo2() {
        next(new SimpleWebSocketListener() {
            @Override
            public void onMessage(WebSocket socket, String text) throws IOException {
                //做个拦截
                super.onMessage(socket, text);
            }
        }).next(new SimpleWebSocketListener() {
            @Override
            public void onMessage(WebSocket socket, String text) throws IOException {
                //开始做业务
                super.onMessage(socket, text);
            }
        });
    }
}
