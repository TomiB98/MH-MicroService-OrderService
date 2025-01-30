package com.example.order_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class ExceptionHandlers {

    @ExceptionHandler(NoOrdersFoundException.class)
    public ResponseEntity<String> orderExceptionHandler(NoOrdersFoundException noOrdersFoundException){
        return new ResponseEntity<>(noOrdersFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StatusException.class)
    public ResponseEntity<String> orderExceptionHandler(StatusException statusException){
        return new ResponseEntity<>(statusException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserIdNullException.class)
    public ResponseEntity<String> orderExceptionHandler(UserIdNullException userIdNullException){
        return new ResponseEntity<>(userIdNullException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StockException.class)
    public ResponseEntity<String> orderExceptionHandler(StockException stockException){
        return new ResponseEntity<>(stockException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoAccesGrantedException.class)
    public ResponseEntity<String> orderExceptionHandler(NoAccesGrantedException noAccesGrantedException){
        return new ResponseEntity<>(noAccesGrantedException.getMessage(), HttpStatus.FORBIDDEN);
    }
}
