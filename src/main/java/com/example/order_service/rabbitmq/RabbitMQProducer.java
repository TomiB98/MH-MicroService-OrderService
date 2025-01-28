package com.example.order_service.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TopicExchange rollbackExchange;

    public void sendRollbackMessage(Long productId, Integer quantity) {
        String message = productId + "," + quantity;
        rabbitTemplate.convertAndSend(rollbackExchange.getName(), "rollback.stock", message);
    }
}
