package org.onstage.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketMessageService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketMessageService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendMessage(String topic, Object webSocketMessage) {
        messagingTemplate.convertAndSend(topic, webSocketMessage);
    }

}
