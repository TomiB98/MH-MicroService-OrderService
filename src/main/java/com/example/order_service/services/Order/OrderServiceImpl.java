package com.example.order_service.services.Order;

import com.example.order_service.dtos.*;
import com.example.order_service.exceptions.NoOrdersFoundException;
import com.example.order_service.exceptions.StatusException;
import com.example.order_service.exceptions.StockException;
import com.example.order_service.exceptions.UserIdNullException;
import com.example.order_service.models.OrderEntity;
import com.example.order_service.models.OrderItemEntity;
import com.example.order_service.models.OrderStatus;
import com.example.order_service.rabbitmq.RabbitMQProducer;
import com.example.order_service.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RabbitMQProducer rabbitMQProducer;


    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private static final String USER_SERVICE_URL = "http://localhost:8081/api/user";
    private static final String PRODUCT_SERVICE_URL = "http://localhost:8082/api/product";
    //private static final String EMAIL_SERVICE_URL = "http://localhost:8084/api/email";

    @Override
    public OrderEntity getOrderById(Long id) throws NoOrdersFoundException {
        return orderRepository.findById(id).orElseThrow( () -> new NoOrdersFoundException("Order with ID " + id + " not found."));
    }


    @Override
    public OrderDTO getOrderDTOById(Long id) throws NoOrdersFoundException {
        return new OrderDTO(getOrderById(id));
    }


    @Override
    public List<OrderEntity> getAllOrders() throws NoOrdersFoundException {
        List<OrderEntity> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            throw new NoOrdersFoundException("No orders found.");
        }
        return orders;
    }


    @Override
    public List<UserOrderDTO> getAllUserOrders(Long userId, String email) throws NoOrdersFoundException {
        // Retrives all user orders in a list
        List<OrderEntity> userOrders = orderRepository.findByUserId(userId);

        // Verifies if the user has orders
        if (userOrders.isEmpty()) {
            throw new NoOrdersFoundException("No orders found for user ID: " + userId);
        }

        // Creates the dto with the product details
        return userOrders.stream().map(order -> {
            List<UserOrderItemDTO> orderItems = order.getOrderItemList().stream()
                    .map(this::getProductDetails)
                    .toList();
            return new UserOrderDTO(order.getId(), email, order.getOrderTotal(), order.getStatus().name(), orderItems);
        }).toList();
    }

    // Retrives product details (name y price) from product-service and creates the dto
    private UserOrderItemDTO getProductDetails(OrderItemEntity orderItem) {

        String productName = restTemplate.getForObject(PRODUCT_SERVICE_URL + "/name" + "/" + orderItem.getProductId(), String.class);
        Double productPrice = restTemplate.getForObject(PRODUCT_SERVICE_URL + "/price" + "/" + orderItem.getProductId(), Double.class);

        return new UserOrderItemDTO(orderItem.getProductId(), productName, productPrice, orderItem.getQuantity());
    }


    @Override
    @Transactional
    public void createNewOrder(NewOrder newOrder, String userEmail, Long userId) throws Exception {
        try {
            validateNewOrder(newOrder);

            // Creates The arrayList for OrderItemEmailDTO
            List<OrderEmailDTO.OrderItemEmailDTO> emailItems = new ArrayList<>();

            // Validates if there's enough stock and brings thr products names and prices from product-service, and adds it to emailDTO list
            Double count = 0.00;
            for (NewOrderItem item : newOrder.orderItems()) {
                validateOrderItemsStock(item);

                String productName = restTemplate.getForObject(PRODUCT_SERVICE_URL + "/name/" + item.productId(), String.class);
                Double productPrice = restTemplate.getForObject(PRODUCT_SERVICE_URL + "/price/" + item.productId(), Double.class);

                count += productPrice * item.quantity();
                emailItems.add(new OrderEmailDTO.OrderItemEmailDTO(item.productId(), productName, productPrice, item.quantity()));
            }

            // Calculates order total
            Double orderTotal = count;

            // Transforms the status from string to enum
            OrderStatus status = OrderStatus.valueOf(newOrder.status());

            OrderEntity order = new OrderEntity(userId, status, orderTotal);

            // Creates list of order items in the order
            List<OrderItemEntity> orderItems = newOrder.orderItems().stream()
                    .map(item -> new OrderItemEntity(order, item.productId(), item.quantity()))
                    .toList();

            // Reduce stock for every item in the order
            for (NewOrderItem item : newOrder.orderItems()) {
                reduceStock(item.productId(), item.quantity());
                logger.info("Stock reducido para producto ID: {} con cantidad: {}", item.productId(), item.quantity());
            }

            // Lanzar una excepción para simular un fallo y devolucion se stock
            //throw new RuntimeException("Forzando el fallo en la creación de la orden");

            order.setOrderItemList(orderItems);
            saveOrder(order);

            // Creates DTO to sent
            OrderEmailDTO orderEmailDTO = new OrderEmailDTO(userId, userEmail, orderTotal, emailItems);

            // Sends DTO with rabbit to email-service to send the email
            rabbitMQProducer.sendOrderEmail(orderEmailDTO);

        } catch (HttpClientErrorException.NotFound e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new NoOrdersFoundException("Product not found: " + e.getResponseBodyAsString());

        } catch (NoOrdersFoundException | StatusException | UserIdNullException | StockException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;

        } catch (Exception e) {
            // Sends a message to notify an error while creating the order and revert the stock
            logger.error("Error creating the order, sending message to RabbitMQ to revert the stock", e);
            for (NewOrderItem item : newOrder.orderItems()) {
                rabbitMQProducer.sendRollbackMessage(item.productId(), item.quantity());
                logger.info("Message sent to RabbitMQ to revert the stock, product ID: {} quantity: {}", item.productId(), item.quantity());
            }
            throw new RuntimeException("An error occurred while creating the order: " + e.getMessage(), e);

        }
    }

    // Validates if there's enough stock
    private void validateOrderItemsStock(NewOrderItem item) throws StockException {
        Integer stock = restTemplate.getForObject(PRODUCT_SERVICE_URL + "/stock/" + item.productId(), Integer.class);
        if (stock == null) {
            throw new StockException("Stock information not available for product with ID " + item.productId());
        }

        if (stock < item.quantity()) {
            throw new StockException("Not enough stock for product ID " + item.productId() + ". Available: " + stock + ", Requested: " + item.quantity());
        }
    }

    // Sends the id and quantity to product-service to reduce stock for every item in the order
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

    // Validates that the status in the order is correct
    public static void validateWrongStatus(String status) throws StatusException {
        if(!status.equals("PENDING") && !status.equals("COMPLETED")) {
            throw new StatusException("Status must only be: PENDING or COMPLETED.");
        }
    }

    public void validateNewOrder(NewOrder newOrder) throws Exception {
        validateWrongStatus(newOrder.status());
    }


}

//Double orderTotal = orderTotalCalculator(newOrder);

//Calculates order total
//    private Double orderTotalCalculator(NewOrder newOrder) {
//        Double count = 0.00;
//
//        for (NewOrderItem item : newOrder.orderItems()) {
//            Double productPrice = restTemplate.getForObject(PRODUCT_SERVICE_URL + "/price" + "/" + item.productId(), Double.class);
//            count += productPrice * item.quantity();
//        }
//        return count;
//    }


//validateNullId(newOrder.userId());


// Validates that the userId is valid
//    public static void validateNullId(Long userid) throws UserIdNullException {
//        if(userid == null) {
//            throw new UserIdNullException("The user id cant be null.");
//        }
//    }


// Obtener email del usuario desde user-service
//String userEmail = getUserEmail(userId);
//    private String getUserEmail(Long id) {
//        return restTemplate.getForObject(USER_SERVICE_URL + "/email"+ "/" + id, String.class);
//    }

//            if (!userExists(newOrder.userId())) {
//                throw new NoOrdersFoundException("User with ID " + newOrder.userId() + " not found.");
//            }
//    private boolean userExists(Long userId) throws NoOrdersFoundException {
//        try {
//            restTemplate.getForObject(USER_SERVICE_URL + "/" + userId, String.class);
//            return true;
//
//        } catch (HttpClientErrorException.NotFound e) {
//            String errorMessage = e.getResponseBodyAsString();
//            throw new NoOrdersFoundException("User not found: " + errorMessage);
//
//        } catch (Exception e) {
//            throw new RuntimeException("An error occurred while checking user existence: " + e.getMessage());
//
//        }
//    }
