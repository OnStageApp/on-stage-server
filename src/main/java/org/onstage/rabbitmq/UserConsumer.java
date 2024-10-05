package org.onstage.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.onstage.notification.WebSocketMessageService;
import org.onstage.notification.model.WebSocketMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static org.onstage.common.config.RabbitMQConfig.USER_QUEUE;

@Service
@RequiredArgsConstructor
public class UserConsumer {
    private final WebSocketMessageService webSocketMessageService;

    @RabbitListener(queues = USER_QUEUE)
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);

        WebSocketMessage wsMessage = new WebSocketMessage("New user message received");
        webSocketMessageService.sendMessage("/topic/messages", wsMessage);
    }
}