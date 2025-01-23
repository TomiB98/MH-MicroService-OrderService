package com.example.order_service.services.Order;

import com.example.order_service.dtos.NewOrder;
import com.example.order_service.dtos.OrderDTO;
import com.example.order_service.dtos.UpdateOrder;
import com.example.order_service.exceptions.NoOrdersFoundException;
import com.example.order_service.models.OrderEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {

    OrderEntity getOrderById(Long id) throws NoOrdersFoundException;
    OrderDTO getOrderDTOById(Long id) throws NoOrdersFoundException;
    List<OrderEntity> getAllOrders() throws NoOrdersFoundException;

    void createNewOrder (NewOrder newOrder) throws Exception;
    OrderEntity saveOrder(OrderEntity newOrder);

    OrderDTO updateOrderById(UpdateOrder updateOrder, Long id) throws Exception;
}
