package com.example.order_service.controllers;

import com.example.order_service.dtos.NewOrder;
import com.example.order_service.dtos.NewOrderItem;
import com.example.order_service.dtos.OrderDTO;
import com.example.order_service.dtos.OrderItemDTO;
import com.example.order_service.exceptions.NoOrdersFoundException;
import com.example.order_service.exceptions.UserIdNullException;
import com.example.order_service.services.Order.OrderService;
import com.example.order_service.services.OrderItem.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orderItem")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    @GetMapping("/")
    public ResponseEntity<String> invalidPath() {
        return ResponseEntity.badRequest().body("The url provided is invalid.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderItemById(@PathVariable Long id) throws NoOrdersFoundException {

        try {
            OrderItemDTO orderItemDTO = orderItemService.getOrderItemDTOById(id);
            return ResponseEntity.ok(orderItemDTO);

        } catch (NoOrdersFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while searching the order item data, try again later.", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @GetMapping("/orderItems")
    public ResponseEntity<?> getAllOrderItems() throws NoOrdersFoundException {

        try {
            return ResponseEntity.ok(orderItemService.getAllOrderItems());

        } catch (NoOrdersFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while searching the order items data, try again later.", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @PostMapping("/orderItems")
    public ResponseEntity<?> createNewOrderItem(@RequestBody NewOrderItem newOrderItem) throws Exception {

        try {
            orderItemService.createNewOrderItem(newOrderItem);
            return new ResponseEntity<>("Order item crated succesfully", HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while creating the order item, try again later.", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
}
