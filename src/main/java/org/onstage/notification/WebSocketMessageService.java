package org.onstage.notification;

import org.onstage.notification.model.WebSocketMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketMessageService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketMessageService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendMessage(String topic, WebSocketMessage webSocketMessage) {
        messagingTemplate.convertAndSend(topic, webSocketMessage);
    }

}
