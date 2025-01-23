package com.example.order_service.services.Order;

import com.example.order_service.dtos.NewOrder;
import com.example.order_service.dtos.NewOrderItem;
import com.example.order_service.dtos.OrderDTO;
import com.example.order_service.dtos.UpdateOrder;
import com.example.order_service.exceptions.NoOrdersFoundException;
import com.example.order_service.exceptions.StatusException;
import com.example.order_service.exceptions.StockException;
import com.example.order_service.exceptions.UserIdNullException;
import com.example.order_service.models.OrderEntity;
import com.example.order_service.models.OrderItemEntity;
import com.example.order_service.models.OrderStatus;
import com.example.order_service.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String USER_SERVICE_URL = "http://localhost:8081/api/user";
    private static final String PRODUCT_SERVICE_URL = "http://localhost:8082/api/product";

    @Override
    public OrderEntity getOrderById(Long id) throws NoOrdersFoundException {
        return orderRepository.findById(id).orElseThrow( () -> new NoOrdersFoundException("Order with ID " + id + " not found."));
    }


    @Override
    public OrderDTO getOrderDTOById(Long id) throws NoOrdersFoundException {
        return new OrderDTO(getOrderById(id));
    }


    @Override
    public List<OrderDTO> getAllOrders() throws NoOrdersFoundException {

        List<OrderDTO> orders = orderRepository.findAll().stream()
                .map(OrderDTO::new)
                .collect(Collectors.toList());

        if (orders.isEmpty()) {
            throw new NoOrdersFoundException("There are no orders.");
        }

        return orders;
    }


    @Override
    public void createNewOrder(NewOrder newOrder) throws Exception {
        try {
            validateNewOrder(newOrder);

            if (!userExists(newOrder.userId())) {
                throw new NoOrdersFoundException("User with ID " + newOrder.userId() + " not found.");
            }

            for (NewOrderItem item : newOrder.orderItems()) {
                validateOrderItemsStock(item);
            }

            OrderStatus status = OrderStatus.valueOf(newOrder.status());
            OrderEntity order = new OrderEntity(newOrder.userId(), status);

            // Lista de items de la orden
            List<OrderItemEntity> orderItems = newOrder.orderItems().stream()
                    .map(item -> new OrderItemEntity(order, item.productId(), item.quantity()))
                    .toList();

            // Reducir el stock de cada item
            for (NewOrderItem item : newOrder.orderItems()) {
                reduceStock(item.productId(), item.quantity());
            }

            order.setOrderItemList(orderItems);
            saveOrder(order);

        } catch (HttpClientErrorException.NotFound e) {
            throw new NoOrdersFoundException("Product not found: " + e.getResponseBodyAsString());

        } catch (NoOrdersFoundException | StatusException | UserIdNullException | StockException e) {
            throw e;

        } catch (Exception e) {
            throw new RuntimeException("An error occurred while creating the order: " + e.getMessage(), e);

        }
    }

    private boolean userExists(Long userId) throws NoOrdersFoundException {
        try {
            restTemplate.getForObject(USER_SERVICE_URL + "/" + userId, String.class);
            return true;

        } catch (HttpClientErrorException.NotFound e) {
            String errorMessage = e.getResponseBodyAsString();
            throw new NoOrdersFoundException("User not found: " + errorMessage);

        } catch (Exception e) {
            throw new RuntimeException("An error occurred while checking user existence: " + e.getMessage());

        }
    }

    private void validateOrderItemsStock(NewOrderItem item) throws StockException {
        Integer stock = restTemplate.getForObject(PRODUCT_SERVICE_URL + "/stock/" + item.productId(), Integer.class);
        if (stock == null) {
            throw new StockException("Stock information not available for product with ID " + item.productId());
        }

        if (stock < item.quantity()) {
            throw new StockException("Not enough stock for product ID " + item.productId() + ". Available: " + stock + ", Requested: " + item.quantity());
        }
    }

    private void reduceStock(Long productId, Integer quantity) throws NoOrdersFoundException {
        try {
            restTemplate.put(PRODUCT_SERVICE_URL + "/" + productId + "/reduce-stock?quantity=" + quantity, null);

        } catch (HttpClientErrorException.NotFound e) {
            throw new NoOrdersFoundException("Product not found: " + e.getResponseBodyAsString());

        } catch (Exception e) {
            throw new RuntimeException("Error while reducing product stock: " + e.getMessage());

        }
    }


    @Override
    public OrderEntity saveOrder(OrderEntity order) {
        return orderRepository.save(order);
    }


    @Override
    public OrderDTO updateOrderById(UpdateOrder updateOrder, Long id) throws Exception {

        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(()-> new NoOrdersFoundException("Order with ID " + id + " not found."));

        validateUpdatedOrder(updateOrder);

        if (!updateOrder.status().isBlank()) {
            OrderStatus status = OrderStatus.valueOf(updateOrder.status());
            order.setStatus(status);
        }

        orderRepository.save(order);
        return new OrderDTO(order);
    }

    //Validations
    public void validateUpdatedOrder(UpdateOrder updateOrder) throws Exception {
        validateWrongStatus(updateOrder.status());
    }

    public static void validateWrongStatus(String status) throws StatusException {
        if(!status.equals("PENDING") && !status.equals("COMPLETED")) {
            throw new StatusException("Status must only be: PENDING or COMPLETED.");
        }
    }

    public void validateNewOrder(NewOrder newOrder) throws Exception {
        validateWrongStatus(newOrder.status());
        validateNullId(newOrder.userId());
    }

    public static void validateNullId(Long userid) throws UserIdNullException {
        if(userid == null) {
            throw new UserIdNullException("The user id cant be null.");
        }
    }
}
