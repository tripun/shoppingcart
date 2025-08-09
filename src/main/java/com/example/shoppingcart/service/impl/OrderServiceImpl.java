package com.example.shoppingcart.service.impl;

import com.example.shoppingcart.model.postgres.Order;
import com.example.shoppingcart.repository.jpa.OrderRepository;
import com.example.shoppingcart.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;

    @Override
    public Order createOrder(Order order) {
        log.info("Creating order for user: {}", order.getUserId());
        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(String orderId) {
        log.info("Fetching order with ID: {}", orderId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
    }

    @Override
    public List<Order> getOrdersByUserId(String userId) {
        log.info("Fetching orders for user: {}", userId);
        return orderRepository.findByUserId(userId);
    }
}