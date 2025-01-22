package com.example.order_service.exceptions;

public class NoOrdersFoundException extends Exception {
    public NoOrdersFoundException(String message) {
        super(message);
    }
}
