package org.onstage.websocket;


import lombok.RequiredArgsConstructor;
import org.onstage.rabbitmq.UserProducer;
import org.onstage.websocket.model.WebSocketMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class MessageRestController {
    private final UserProducer userProducer;

    @PostMapping("/send-message")
    public void sendMessage(@RequestBody WebSocketMessage message) {
        System.out.println("Sent message via REST: " + message);
        userProducer.sendMessage(message.content());
    }
}