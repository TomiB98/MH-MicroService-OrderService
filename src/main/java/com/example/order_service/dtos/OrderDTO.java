package com.example.order_service.dtos;

import com.example.order_service.models.OrderEntity;
import com.example.order_service.models.OrderItemEntity;
import com.example.order_service.models.OrderStatus;

import java.util.ArrayList;
import java.util.List;

public class OrderDTO {

    private Long id;
    private Long userId;
    private List<OrderItemEntity> orderItemList = new ArrayList<>();
    private OrderStatus status;

    public OrderDTO(OrderEntity order) {
        id = order.getId();
        userId = order.getUserId();
        orderItemList = order.getOrderItemList();
        status = order.getStatus();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public List<OrderItemEntity> getOrderItemList() {
        return orderItemList;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
