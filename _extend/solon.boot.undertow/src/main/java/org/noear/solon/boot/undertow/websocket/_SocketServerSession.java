package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.Callback;
import org.noear.solon.core.message.Session;
import org.noear.solon.core.message.Message;
import org.noear.solon.socketd.ProtocolManager;
import org.noear.solon.socketd.SessionBase;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.*;

public class _SocketServerSession extends SessionBase {
    public static Map<WebSocketChannel, Session> sessions = new HashMap<>();

    public static Session get(WebSocketChannel real) {
        Session tmp = sessions.get(real);
        if (tmp == null) {
            synchronized (real) {
                tmp = sessions.get(real);
                if (tmp == null) {
                    tmp = new _SocketServerSession(real);
                    sessions.put(real, tmp);
                }
            }
        }

        return tmp;
    }

    public static void remove(WebSocketChannel real) {
        sessions.remove(real);
    }


    WebSocketChannel real;

    public _SocketServerSession(WebSocketChannel real) {
        this.real = real;
    }

    @Override
    public Object real() {
        return real;
    }

    private String _sessionId = Utils.guid();

    @Override
    public String sessionId() {
        return _sessionId;
    }

    @Override
    public MethodType method() {
        return MethodType.WEBSOCKET;
    }

    private URI _uri;

    @Override
    public URI uri() {
        if (_uri == null) {
            _uri = URI.create(real.getUrl());
        }

        return _uri;
    }

    private String _path;

    @Override
    public String path() {
        if (_path == null) {
            _path = uri().getPath();
        }

        return _path;
    }

    @Override
    public void sendAsync(String message, Callback callback) {

        if (Solon.global().enableWebSocketD()) {
            ByteBuffer buf = ProtocolManager.encode(Message.wrap(message));
            WebSockets.sendBinary(buf, real, new _CallbackHolder(callback));
        } else {
            WebSockets.sendText(message, real, new _CallbackHolder(callback));
        }
    }

    @Override
    public void sendAsync(Message message, Callback callback) {
        super.send(message);


        if (Solon.global().enableWebSocketD()) {
            ByteBuffer buf = ProtocolManager.encode(message);
            WebSockets.sendBinary(buf, real, new _CallbackHolder(callback));
        } else {
            if (message.isString()) {
                WebSockets.sendText(message.bodyAsString(), real, new _CallbackHolder(callback));
            } else {
                ByteBuffer buf = ByteBuffer.wrap(message.body());
                WebSockets.sendBinary(buf, real, new _CallbackHolder(callback));
            }
        }
    }

    @Override
    public void send(String message) {
        try {
            if (Solon.global().enableWebSocketD()) {
                ByteBuffer buf = ProtocolManager.encode(Message.wrap(message));
                WebSockets.sendBinaryBlocking(buf, real);
            } else {
                WebSockets.sendTextBlocking(message, real);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void send(Message message) {
        super.send(message);

        try {
            if (Solon.global().enableWebSocketD()) {
                ByteBuffer buf = ProtocolManager.encode(message);
                WebSockets.sendBinaryBlocking(buf, real);
            } else {
                if (message.isString()) {
                    WebSockets.sendTextBlocking(message.bodyAsString(), real);
                } else {
                    ByteBuffer buf = ByteBuffer.wrap(message.body());
                    WebSockets.sendBinaryBlocking(buf, real);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void close() throws IOException {
        real.close();
        sessions.remove(real);
    }

    @Override
    public boolean isValid() {
        return real.isOpen();
    }

    @Override
    public boolean isSecure() {
        return real.isSecure();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return real.getSourceAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return real.getDestinationAddress();
    }

    @Override
    public void setAttachment(Object obj) {
        real.setAttribute("attachment", obj);
    }

    @Override
    public <T> T getAttachment() {
        return (T) real.getAttribute("attachment");
    }

    @Override
    public Collection<Session> getOpenSessions() {
        return new ArrayList<>(sessions.values());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        _SocketServerSession that = (_SocketServerSession) o;
        return Objects.equals(real, that.real);
    }

    @Override
    public int hashCode() {
        return Objects.hash(real);
    }
}
