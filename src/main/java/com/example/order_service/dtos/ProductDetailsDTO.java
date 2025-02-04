package com.example.order_service.dtos;

public class ProductDetailsDTO {
    private Long id;
    private String name, productdescription;
    private Double productprice;
    private Integer stock;

    public ProductDetailsDTO(Long id, String name, String productdescription, Double productprice, Integer stock) {
        this.id = id;
        this.name = name;
        this.productdescription = productdescription;
        this.productprice = productprice;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProductdescription() {
        return productdescription;
    }

    public Double getProductprice() {
        return productprice;
    }

    public Integer getStock() {
        return stock;
    }
}
