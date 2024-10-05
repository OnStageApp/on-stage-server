package org.onstage.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static org.onstage.common.config.RabbitMQConfig.USER_QUEUE;

@Service
public class UserConsumer {

    @RabbitListener(queues = USER_QUEUE)
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);
    }
}