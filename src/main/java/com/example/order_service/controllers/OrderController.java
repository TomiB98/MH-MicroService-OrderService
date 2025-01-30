package com.example.order_service.controllers;

import com.example.order_service.dtos.NewOrder;
import com.example.order_service.dtos.OrderDTO;
import com.example.order_service.dtos.UpdateOrder;
import com.example.order_service.dtos.UserOrderDTO;
import com.example.order_service.exceptions.*;
import com.example.order_service.models.OrderEntity;
import com.example.order_service.services.Order.OrderService;
import com.example.order_service.services.TokenDataServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private TokenDataServiceImpl tokenDataService;

    @GetMapping("/")
    public ResponseEntity<String> invalidPath() {
        return ResponseEntity.badRequest().body("The url provided is invalid.");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Gets an order data with the id", description = "Receives an id and returns all the data of the specified order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data successfully received."),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid id.")
    })
    public ResponseEntity<?> getOrderById(@PathVariable Long id, HttpServletRequest request) throws NoOrdersFoundException {

        try {

            String authenticatedUserRole = tokenDataService.getRole(request);

            if (!authenticatedUserRole.equals("ADMIN")) {
                return new ResponseEntity<>("Forbidden: You cannot access this data.", HttpStatus.FORBIDDEN);
            }

            OrderDTO orderDTO = orderService.getOrderDTOById(id);
            return ResponseEntity.ok(orderDTO);

        } catch (NoOrdersFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while searching the order data, try again later.", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @GetMapping("/orders")
    @Operation(summary = "Gets all orders data in the db", description = "Returns all the orders in the db.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data successfully received."),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid id.")
    })
    public ResponseEntity<?> getAllOrders(HttpServletRequest request) {
        try {

            String authenticatedUserRole = tokenDataService.getRole(request);

            if (!authenticatedUserRole.equals("ADMIN")) {
                return new ResponseEntity<>("Forbidden: You cannot access this data.", HttpStatus.FORBIDDEN);
            }

            List<OrderEntity> orders = orderService.getAllOrders();
            List<OrderDTO> orderDTOs = orders.stream()
                    .map(OrderDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(orderDTOs);

        } catch (NoOrdersFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while fetching the orders, try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/orders/user/{userId}")
    @Operation(summary = "Gets all the orders of an user with the id", description = "Returns all the orders of the specified user with data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data successfully received."),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid id."),
            @ApiResponse(responseCode = "404", description = "No orders found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    public ResponseEntity<?> getAllUserOrders(@PathVariable Long userId, HttpServletRequest request) throws Exception {

        try {
            String authenticatedUserRole = tokenDataService.getRole(request);
            String authenticatedUserEmail = tokenDataService.getEmail(request);

            if (authenticatedUserRole.equals("ADMIN")) {
                List<UserOrderDTO> orders = orderService.getAllUserOrders(userId, authenticatedUserEmail);
                return ResponseEntity.ok(orders);

            } else throw new NoAccesGrantedException("Forbidden: You cannot access another user's data.");

        } catch (NoOrdersFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (NoAccesGrantedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching the orders, try again later.");

        }

    }


    @GetMapping("/orders/user")
    @Operation(summary = "Gets all the orders of an user with the id", description = "Returns all the orders of the specified user with data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data successfully received."),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid id."),
            @ApiResponse(responseCode = "404", description = "No orders found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    public ResponseEntity<?> getAllUserLoggedOrders(HttpServletRequest request) throws Exception {

        try {
            Long authenticatedUserId = tokenDataService.getId(request);
            String authenticatedUserEmail = tokenDataService.getEmail(request);

            List<UserOrderDTO> orders = orderService.getAllUserOrders(authenticatedUserId, authenticatedUserEmail);
            return ResponseEntity.ok(orders);

        } catch (NoOrdersFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching the orders, try again later.");

        }

    }


    @PostMapping("/orders")
    @Operation(summary = "Creates a new order", description = "Receives a user id, status, a list of items and creates a new order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order successfully created."),
            @ApiResponse(responseCode = "403", description = "Unauthorized to create an order for another user."),
            @ApiResponse(responseCode = "409", description = "Bad request, invalid data.")
    })
    public ResponseEntity<?> createNewOrder(@RequestBody NewOrder newOrder, HttpServletRequest request) throws Exception {

        try {

            String authenticatedUserEmail = tokenDataService.getEmail(request);
            Long authenticatedUserId = tokenDataService.getId(request);

            orderService.createNewOrder(newOrder, authenticatedUserEmail, authenticatedUserId);
            return new ResponseEntity<>("Order crated succesfully", HttpStatus.CREATED);

        } catch (UserIdNullException | NoOrdersFoundException | StockException | StatusException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while creating the order, try again later.", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }


    @PutMapping("orders/{id}")
    @Operation(summary = "Updates an order", description = "Receives an id and updates the assigned order, you can update all the data or independently if you leave one blank it will retrieve the old value.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order successfully updated."),
            @ApiResponse(responseCode = "403", description = "Unauthorized to update this order."),
            @ApiResponse(responseCode = "409", description = "Bad request, order not found.")
    })
    public ResponseEntity<?> updateOrderById(@RequestBody UpdateOrder updateOrder, @PathVariable Long id, HttpServletRequest request) throws Exception {

        try {

            String authenticatedUserRole = tokenDataService.getRole(request);

            if (!authenticatedUserRole.equals("ADMIN")) {
                return new ResponseEntity<>("Forbidden: You cannot update this data.", HttpStatus.FORBIDDEN);
            }

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
