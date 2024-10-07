package org.onstage.notification;


import lombok.RequiredArgsConstructor;
import org.onstage.notification.model.WebSocketMessage;
import org.onstage.rabbitmq.UserProducer;
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