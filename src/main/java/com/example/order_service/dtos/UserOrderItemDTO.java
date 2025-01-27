package com.example.order_service.dtos;

public class UserOrderItemDTO {

    private Long productId;
    private String productName;
    private Double productPrice;
    private Integer quantity;

    public UserOrderItemDTO(Long productId, String productName, Double productPrice, Integer quantity) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
