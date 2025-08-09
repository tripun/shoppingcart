package com.example.shoppingcart.model.dynamo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Order {

    private String orderId;
    private String userId;
    private String cartId;
    private LocalDateTime orderDate;
    private BigDecimal originalTotalPrice;
    private BigDecimal totalDiscount;
    private BigDecimal finalTotalPrice;
    private String currency;
    private String region;
    private String paymentMethod;
    private String status;
    private List<OrderItem> orderItems;
    private List<AppliedDiscount> appliedDiscounts;

    @DynamoDbPartitionKey
    public String getOrderId() { return orderId; }

    @DynamoDbSortKey
    public String getUserId() { return userId; }
}
