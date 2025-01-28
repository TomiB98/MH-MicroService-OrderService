package com.example.order_service.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private static final String ROLLBACK_QUEUE = "rollbackStockQueue";
    private static final String ROLLBACK_EXCHANGE = "rollbackExchange";
    private static final String ROLLBACK_ROUTING_KEY = "rollback.stock";

    @Bean
    public Queue rollbackQueue() {
        return new Queue(ROLLBACK_QUEUE, false);
    }

    @Bean
    public TopicExchange rollbackExchange() {
        return new TopicExchange(ROLLBACK_EXCHANGE);
    }

    @Bean
    public Binding rollbackBinding(Queue rollbackQueue, TopicExchange rollbackExchange) {
        return BindingBuilder.bind(rollbackQueue).to(rollbackExchange).with(ROLLBACK_ROUTING_KEY);
    }
}