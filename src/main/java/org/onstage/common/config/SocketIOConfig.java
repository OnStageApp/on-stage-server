package org.onstage.common.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SocketIOConfig {
    @Value("${socket-server.host}")
    private String host;

    @Value("${socket-server.port}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);

        // Add socket configuration
        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setReuseAddress(true);
        config.setSocketConfig(socketConfig);

        // Add other useful configurations
        config.setAllowCustomRequests(true);
        config.setPingTimeout(60000);
        config.setPingInterval(25000);
        config.setUpgradeTimeout(10000);

        try {
            return new SocketIOServer(config);
        } catch (Exception e) {
            log.error("Failed to create SocketIOServer: {}", e.getMessage(), e);
            throw new RuntimeException("Could not create SocketIOServer", e);
        }
    }
}