package com.example.order_service.services.OrderItem;

import com.example.order_service.dtos.NewOrder;
import com.example.order_service.dtos.NewOrderItem;
import com.example.order_service.dtos.OrderDTO;
import com.example.order_service.dtos.OrderItemDTO;
import com.example.order_service.exceptions.NoOrdersFoundException;
import com.example.order_service.models.OrderEntity;
import com.example.order_service.models.OrderItemEntity;
import com.example.order_service.models.OrderStatus;
import com.example.order_service.repositories.OrderItemRepository;
import com.example.order_service.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public OrderItemEntity getOrderItemById(Long id) throws NoOrdersFoundException {
        return orderItemRepository.findById(id).orElseThrow( () -> new NoOrdersFoundException("OrderItem with ID " + id + " not found."));
    }


    @Override
    public OrderItemDTO getOrderItemDTOById(Long id) throws NoOrdersFoundException {
        return new OrderItemDTO(getOrderItemById(id));
    }


    @Override
    public List<OrderItemDTO> getAllOrderItems() throws NoOrdersFoundException {

        List<OrderItemDTO> orderItems = orderItemRepository.findAll().stream()
                .map(OrderItemDTO::new)
                .collect(Collectors.toList());

        if (orderItems.isEmpty()) {
            throw new NoOrdersFoundException("There are no orders items.");
        }

        return orderItems;
    }

    @Override
    public void createNewOrderItem(NewOrderItem newOrderItem) throws Exception {
        //validateNewOrder(newOrder);
        OrderEntity order = orderRepository.findById(newOrderItem.orderId())
                .orElseThrow(() -> new NoOrdersFoundException("Order with ID " + newOrderItem.orderId() + " not found."));
        OrderItemEntity orderItem = new OrderItemEntity(order, newOrderItem.productId(), newOrderItem.quantity());
        saveOrderItem(orderItem);
    }


    @Override
    public OrderItemEntity saveOrderItem(OrderItemEntity orderItem) {
        return orderItemRepository.save(orderItem);
    }
}
