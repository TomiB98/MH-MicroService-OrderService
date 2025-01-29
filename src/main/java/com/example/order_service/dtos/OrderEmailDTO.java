package com.example.order_service.dtos;

import java.util.List;

public class OrderEmailDTO {
    private Long userId;
    private Double totalAmount;
    private List<OrderItemEmailDTO> items;

    public OrderEmailDTO(Long userId, Double totalAmount, List<OrderItemEmailDTO> items) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    public Long getUserId() { return userId; }
    public Double getTotalAmount() { return totalAmount; }
    public List<OrderItemEmailDTO> getItems() { return items; }

    public static class OrderItemEmailDTO {
        private Long productId;
        private String productName;
        private Double productPrice;
        private Integer quantity;

        public OrderItemEmailDTO(Long productId, String productName, Double productPrice, Integer quantity) {
            this.productId = productId;
            this.productName = productName;
            this.productPrice = productPrice;
            this.quantity = quantity;
        }

        public Long getProductId() { return productId; }
        public String getProductName() { return productName; }
        public Double getProductPrice() { return productPrice; }
        public Integer getQuantity() { return quantity; }
    }
}
