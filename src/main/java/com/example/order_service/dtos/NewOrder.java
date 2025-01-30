package com.example.order_service.dtos;

import java.util.List;

public record NewOrder(String status, List<NewOrderItem> orderItems) { }
