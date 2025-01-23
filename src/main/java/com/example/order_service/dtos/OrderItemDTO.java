package com.example.order_service.dtos;

import com.example.order_service.models.OrderItemEntity;

public class OrderItemDTO {

    private Long id;
    private Long productId;
    private Integer quantity;

    public OrderItemDTO(OrderItemEntity orderItem) {
        id = orderItem.getId();
        productId = orderItem.getProductId();
        quantity = orderItem.getQuantity();
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
