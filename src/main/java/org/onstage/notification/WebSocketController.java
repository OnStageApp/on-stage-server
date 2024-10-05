package org.onstage.notification;

import lombok.RequiredArgsConstructor;
import org.onstage.notification.model.WebSocketMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final WebSocketMessageService webSocketMessageService;

    // Testing POST call for websocket
    @PostMapping("/api/send-message")
    public void sendMessage(@RequestBody WebSocketMessage message) {
        webSocketMessageService.sendMessage("/topic/messages", message);
    }
}
