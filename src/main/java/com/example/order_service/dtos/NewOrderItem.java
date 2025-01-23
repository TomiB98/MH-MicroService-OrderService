package com.example.order_service.dtos;

public record NewOrderItem(Long productId, Integer quantity, Long orderId) { }
