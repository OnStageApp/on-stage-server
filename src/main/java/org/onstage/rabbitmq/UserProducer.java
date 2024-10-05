package org.onstage.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.onstage.common.config.RabbitMQConfig.USER_EXCHANGE;
import static org.onstage.common.config.RabbitMQConfig.USER_ROUTING_KEY;

@Service
public class UserProducer {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public UserProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(USER_EXCHANGE, USER_ROUTING_KEY, message);
        System.out.println("Message sent: " + message);
    }
}