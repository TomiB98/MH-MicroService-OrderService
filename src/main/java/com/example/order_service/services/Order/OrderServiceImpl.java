package com.example.order_service.services.Order;

import com.example.order_service.dtos.NewOrder;
import com.example.order_service.dtos.OrderDTO;
import com.example.order_service.dtos.UpdateOrder;
import com.example.order_service.exceptions.NoOrdersFoundException;
import com.example.order_service.exceptions.StatusException;
import com.example.order_service.exceptions.UserIdNullException;
import com.example.order_service.models.OrderEntity;
import com.example.order_service.models.OrderStatus;
import com.example.order_service.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

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
        validateNewOrder(newOrder);
        OrderStatus status = OrderStatus.valueOf(newOrder.status());
        OrderEntity order = new OrderEntity(newOrder.userId(), status);
        saveOrder(order);
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
