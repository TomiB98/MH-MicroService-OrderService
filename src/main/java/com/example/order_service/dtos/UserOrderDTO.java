package com.example.order_service.dtos;

import java.util.List;

public class UserOrderDTO {

    private Long id;
    private String userEmail;
    private Double orderTotal;
    private String status;
    private List<UserOrderItemDTO> orderItems;

    public UserOrderDTO(Long id, String userEmail, Double orderTotal, String status, List<UserOrderItemDTO> orderItems) {
        this.id = id;
        this.userEmail = userEmail;
        this.orderTotal = orderTotal;
        this.status = status;
        this.orderItems = orderItems;
    }

    public Long getId() {
        return id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Double getOrderTotal() {
        return orderTotal;
    }

    public String getStatus() {
        return status;
    }

    public List<UserOrderItemDTO> getOrderItems() {
        return orderItems;
    }
}
