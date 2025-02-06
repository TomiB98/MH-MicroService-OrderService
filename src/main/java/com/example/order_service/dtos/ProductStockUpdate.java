package com.example.order_service.dtos;

public class ProductStockUpdate {
    private Long productId;
    private Integer quantity;

    public ProductStockUpdate(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
