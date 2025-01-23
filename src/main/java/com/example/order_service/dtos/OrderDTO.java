package com.example.order_service.dtos;

import com.example.order_service.models.OrderEntity;
import com.example.order_service.models.OrderItemEntity;
import com.example.order_service.models.OrderStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDTO {

    private Long id;
    private Long userId;
    private List<OrderItemDTO> orderItemList = new ArrayList<>();
    private OrderStatus status;

    public OrderDTO(OrderEntity order) {
        id = order.getId();
        userId = order.getUserId();
        orderItemList = order.getOrderItemList().stream()
                .map(OrderItemDTO::new)
                .collect(Collectors.toList());
        status = order.getStatus();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public List<OrderItemDTO> getOrderItemList() {
        return orderItemList;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
