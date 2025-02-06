package com.example.order_service.dtos;

import java.util.List;

public class ReduceStockRequest {
    private List<ProductStockUpdate> products;

    public ReduceStockRequest(List<ProductStockUpdate> products) {
        this.products = products;
    }

    public List<ProductStockUpdate> getProducts() {
        return products;
    }
}
