package com.example.order_service.services.OrderItem;

import com.example.order_service.dtos.NewOrderItem;
import com.example.order_service.dtos.OrderItemDTO;
import com.example.order_service.exceptions.NoOrdersFoundException;
import com.example.order_service.models.OrderItemEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderItemService {

    OrderItemEntity getOrderItemById(Long id) throws NoOrdersFoundException;
    OrderItemDTO getOrderItemDTOById(Long id) throws NoOrdersFoundException;
    List<OrderItemDTO> getAllOrderItems() throws NoOrdersFoundException;

    void createNewOrderItem (NewOrderItem newOrderItem) throws Exception;
    OrderItemEntity saveOrderItem(OrderItemEntity newOrderItem);

}
