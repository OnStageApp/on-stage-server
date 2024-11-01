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
    private final Map<String, SocketIOClient> deviceSession = new ConcurrentHashMap<>();
    private boolean isRunning = false;

    public SocketIOService(SocketIOServer server) {
        this.server = server;
        setupEventListeners();
    }

    private void setupEventListeners() {
        server.addConnectListener(client -> {
            String deviceId = client.getHandshakeData().getSingleUrlParam("deviceId");
            log.info("Connect attempt - deviceId from handshake: {}", deviceId);

            if (deviceId != null) {
                deviceSession.put(deviceId, client);
                log.info("Device {} connected. Active sessions: {}", deviceId, deviceSession.size());
                SocketIOClient storedClient = deviceSession.get(deviceId);
                log.info("Stored client check - DeviceId: {}, Client stored: {}, Sessions size: {}",
                        deviceId,
                        storedClient != null,
                        deviceSession.size()
                );
            } else {
                log.warn("Client attempted to connect without deviceId");
                client.disconnect();
            }
        });

        server.addDisconnectListener(client -> {
            String deviceId = client.getHandshakeData().getSingleUrlParam("deviceId");
            if (deviceId != null) {
                deviceSession.remove(deviceId);
                log.info("Device {} disconnected. Remaining sessions: {}", deviceId, deviceSession.size());
            }
        });

    }

    public void sendSocketEvent(String userId, String deviceId, SocketEventType eventName, Object data) {
        log.info("Attempting to send to user {} with device: {}. Active sessions: {}", userId, deviceId, deviceSession.size());
        log.info("Active device sessions: {}", deviceSession.keySet());

        SocketIOClient client = deviceSession.get(deviceId);
        if (client != null && client.isChannelOpen()) {
            try {
                client.sendEvent(eventName.name(), data);
                log.debug("Sent event '{}' to user {} with device {}", eventName, userId, deviceId);
            } catch (Exception e) {
                log.error("Error sending event to user {} with device {}: {}", userId, deviceId, e.getMessage(), e);
                deviceSession.remove(deviceId);
            }
        } else {
            log.warn("Cannot send to user {} with device {}. Client {} or channel not open",
                    userId, deviceId, client == null ? "not found" : "found");
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
                deviceSession.values().forEach(client -> {
                    try {
                        client.disconnect();
                    } catch (Exception e) {
                        log.warn("Error disconnecting client: {}", e.getMessage());
                    }
                });
                deviceSession.clear();

                server.stop();
                isRunning = false;
                log.info("Socket.IO server stopped successfully");
            }
        } catch (Exception e) {
            log.error("Error stopping Socket.IO server: {}", e.getMessage(), e);
        }
    }
}