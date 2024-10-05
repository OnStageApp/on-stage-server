package org.onstage.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    // Song queue configuration
    public static final String SONG_QUEUE = "song-queue";
    public static final String SONG_EXCHANGE = "song-exchange";
    public static final String SONG_ROUTING_KEY = "song.routing.key";
    // User queue configuration
    public static final String USER_QUEUE = "user-queue";
    public static final String USER_EXCHANGE = "user-exchange";
    public static final String USER_ROUTING_KEY = "user.routing.key";
    @Value("${spring.rabbitmq.uri}")
    private String rabbitMqUri;

    @Bean
    public Queue songQueue() {
        return new Queue(SONG_QUEUE, false);
    }

    @Bean
    public Queue userQueue() {
        return new Queue(USER_QUEUE, false);
    }

    @Bean
    public TopicExchange songExchange() {
        return new TopicExchange(SONG_EXCHANGE);
    }

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    @Bean
    public Binding songBinding(Queue songQueue, TopicExchange songExchange) {
        return BindingBuilder.bind(songQueue).to(songExchange).with(SONG_ROUTING_KEY);
    }

    @Bean
    public Binding userBinding(Queue userQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userQueue).to(userExchange).with(USER_ROUTING_KEY);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setUri(rabbitMqUri);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}