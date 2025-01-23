package com.example.order_service.dtos;

import java.util.List;

public record NewOrder(Long userId, String status, List<NewOrderItem> orderItems) { }
