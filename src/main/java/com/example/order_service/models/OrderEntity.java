package com.example.order_service.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Double orderTotal;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItemEntity> orderItemList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public OrderEntity() { }

    public OrderEntity(Long userId, OrderStatus status,Double orderTotal) {
        this.userId = userId;
        this.status = status;
        this.orderTotal = orderTotal;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<OrderItemEntity> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItemEntity> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Double orderTotal) {
        this.orderTotal = orderTotal;
    }
}
