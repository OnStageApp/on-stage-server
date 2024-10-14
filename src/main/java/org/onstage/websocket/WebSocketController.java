package org.onstage.websocket;

import lombok.RequiredArgsConstructor;
import org.onstage.websocket.model.WebSocketMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class WebSocketController {
    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public WebSocketMessage handleMessage(WebSocketMessage message) {
        System.out.println("Received message via WebSocket: " + message);
        return message;
    }
}
