package org.onstage.websocket;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SocketIOService {
    private final SocketIOServer server;

    public SocketIOService(SocketIOServer server) {
        this.server = server;
        server.addConnectListener(client -> log.info("Client connected: {}", client.getSessionId()));
        server.addDisconnectListener(client -> log.info("Client disconnected: {}", client.getSessionId()));
    }

    public void emitEvent(String eventName, Object data) {
        server.getBroadcastOperations().sendEvent(eventName, data);
    }
}
