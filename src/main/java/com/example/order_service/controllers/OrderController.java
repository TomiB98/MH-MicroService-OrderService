package com.example.order_service.controllers;

import com.example.order_service.dtos.NewOrder;
import com.example.order_service.dtos.OrderDTO;
import com.example.order_service.dtos.UpdateOrder;
import com.example.order_service.exceptions.NoOrdersFoundException;
import com.example.order_service.exceptions.StatusException;
import com.example.order_service.exceptions.StockException;
import com.example.order_service.exceptions.UserIdNullException;
import com.example.order_service.services.Order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/")
    public ResponseEntity<String> invalidPath() {
        return ResponseEntity.badRequest().body("The url provided is invalid.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) throws NoOrdersFoundException {

        try {
            OrderDTO orderDTO = orderService.getOrderDTOById(id);
            return ResponseEntity.ok(orderDTO);

        } catch (NoOrdersFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while searching the order data, try again later.", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders() throws NoOrdersFoundException {

        try {
            return ResponseEntity.ok(orderService.getAllOrders());

        } catch (NoOrdersFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while searching the orders data, try again later.", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @PostMapping("/orders")
    public ResponseEntity<?> createNewOrder(@RequestBody NewOrder newOrder) throws Exception {

        try {
            orderService.createNewOrder(newOrder);
            return new ResponseEntity<>("Order crated succesfully", HttpStatus.CREATED);

        } catch (UserIdNullException | NoOrdersFoundException | StockException | StatusException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while creating the order, try again later.", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }


    @PutMapping("orders/{id}")
    public ResponseEntity<?> updateOrderById(@RequestBody UpdateOrder updateOrder, @PathVariable Long id) throws Exception {

        try {
            OrderDTO updatedOrder = orderService.updateOrderById(updateOrder, id);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedOrder);

        } catch (NoOrdersFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (StatusException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while updating the order, try again later.", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
}
