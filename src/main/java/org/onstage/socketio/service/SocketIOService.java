package org.onstage.socketio.service;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.onstage.socketio.SocketEventType;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SocketIOService {
    private final SocketIOServer server;
    private final Map<String, SocketIOClient> userSessions = new ConcurrentHashMap<>();
    private boolean isRunning = false;

    public SocketIOService(SocketIOServer server) {
        this.server = server;
        setupEventListeners();
    }

    private void setupEventListeners() {
        server.addConnectListener(client -> {
            String userId = client.getHandshakeData().getSingleUrlParam("userId");
            log.info("Connect attempt - UserID from handshake: {}", userId);

            if (userId != null) {
                userSessions.put(userId, client);
                log.info("User {} connected. Active sessions: {}", userId, userSessions.size());
                SocketIOClient storedClient = userSessions.get(userId);
                log.info("Stored client check - UserId: {}, Client stored: {}, Sessions size: {}",
                        userId,
                        storedClient != null,
                        userSessions.size()
                );
            } else {
                log.warn("Client attempted to connect without userId");
                client.disconnect();
            }
        });

        server.addDisconnectListener(client -> {
            String userId = client.getHandshakeData().getSingleUrlParam("userId");
            if (userId != null) {
                userSessions.remove(userId);
                log.info("User {} disconnected. Remaining sessions: {}", userId, userSessions.size());
            }
        });

    }

    public void sendToUser(String userId, SocketEventType eventName, Object data) {
        log.info("Attempting to send to user: {}. Active sessions: {}", userId, userSessions.size());
        log.info("Active user sessions: {}", userSessions.keySet());

        SocketIOClient client = userSessions.get(userId);
        if (client != null && client.isChannelOpen()) {
            try {
                client.sendEvent(eventName.name(), data);
                log.debug("Sent event '{}' to user {}", eventName, userId);
            } catch (Exception e) {
                log.error("Error sending event to user {}: {}", userId, e.getMessage(), e);
                userSessions.remove(userId);  // Clean up failed connection
            }
        } else {
            log.warn("Cannot send to user {}. Client {} or channel not open",
                    userId, client == null ? "not found" : "found");
        }
    }

    @PostConstruct
    private void startServer() {
        try {
            if (!isRunning) {
                server.start();
                isRunning = true;
                log.info("Socket.IO server started successfully on {}:{}",
                        server.getConfiguration().getHostname(),
                        server.getConfiguration().getPort());
            }
        } catch (Exception e) {
            log.error("Failed to start Socket.IO server: {}", e.getMessage(), e);
            throw new RuntimeException("Could not start socket.io server", e);
        }
    }

    @PreDestroy
    private void stopServer() {
        try {
            if (isRunning) {
                // Disconnect all clients first
                userSessions.values().forEach(client -> {
                    try {
                        client.disconnect();
                    } catch (Exception e) {
                        log.warn("Error disconnecting client: {}", e.getMessage());
                    }
                });
                userSessions.clear();

                server.stop();
                isRunning = false;
                log.info("Socket.IO server stopped successfully");
            }
        } catch (Exception e) {
            log.error("Error stopping Socket.IO server: {}", e.getMessage(), e);
        }
    }
}