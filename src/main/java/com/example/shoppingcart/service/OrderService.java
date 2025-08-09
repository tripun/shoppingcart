package com.example.shoppingcart.service;

import com.example.shoppingcart.model.postgres.Order;
import java.util.List;

public interface OrderService {
    Order createOrder(Order order);
    Order getOrderById(String orderId);
    List<Order> getOrdersByUserId(String userId);
    // Add other order-related methods as needed
}
