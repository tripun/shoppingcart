package com.example.shoppingcart.controller;

import com.example.shoppingcart.dto.CheckoutResponseDto;
import com.example.shoppingcart.model.postgres.Order;
import com.example.shoppingcart.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    @Operation(summary = "Create an order from a checkout response")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid checkout response"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Order> createOrder(@RequestBody CheckoutResponseDto checkoutResponse, Authentication authentication) {
        String username = authentication.getName();
        log.info("Creating order for user {} from checkout response", username);
        // Map CheckoutResponseDto to Postgres Order entity
        Order order = new Order();
    order.setOrderId(UUID.randomUUID().toString());
        order.setUserId(username);
        order.setOrderDate(java.time.LocalDateTime.now());
        order.setOriginalTotalPrice(java.math.BigDecimal.valueOf(checkoutResponse.getOriginalTotalPrice()));
        order.setTotalDiscount(java.math.BigDecimal.valueOf(checkoutResponse.getTotalDiscount()));
        order.setFinalTotalPrice(java.math.BigDecimal.valueOf(checkoutResponse.getFinalTotalPrice()));
        order.setStatus(Order.OrderStatus.PENDING);
        // Note: mapping order items and applied discounts can be added as needed
        Order saved = orderService.createOrder(order);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable String userId, Authentication authentication) {
        String requester = authentication.getName();
        // Allow user to view their own orders or admins
        if (!requester.equals(userId) && authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    List<Order> orders = orderService.getOrdersByUserId(userId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // TODO: Add endpoint to get order details by ID
    // TODO: Add endpoint to get all orders for a user
}
