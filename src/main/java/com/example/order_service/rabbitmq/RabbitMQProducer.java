package com.example.order_service.rabbitmq;

import com.example.order_service.dtos.OrderEmailDTO;
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

    @Autowired
    private TopicExchange emailExchange;


    public void sendRollbackMessage(Long productId, Integer quantity) {
        String message = productId + "," + quantity;
        rabbitTemplate.convertAndSend(rollbackExchange.getName(), "rollback.stock", message);
    }

    public void sendOrderEmail(OrderEmailDTO orderEmailDTO) {
        rabbitTemplate.convertAndSend(emailExchange.getName(), "order.email", orderEmailDTO);
    }
}
