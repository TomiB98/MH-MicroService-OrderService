package com.example.order_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private static final String ROLLBACK_QUEUE = "rollbackStockQueue";
    private static final String ROLLBACK_EXCHANGE = "rollbackExchange";
    private static final String ROLLBACK_ROUTING_KEY = "rollback.stock";

    private static final String EMAIL_QUEUE = "orderEmailQueue";
    private static final String EMAIL_EXCHANGE = "orderEmailExchange";
    private static final String EMAIL_ROUTING_KEY = "order.email";

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

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

    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, false);
    }

    @Bean
    public TopicExchange emailExchange() {
        return new TopicExchange(EMAIL_EXCHANGE);
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, TopicExchange emailExchange) {
        return BindingBuilder.bind(emailQueue).to(emailExchange).with(EMAIL_ROUTING_KEY);
    }

}