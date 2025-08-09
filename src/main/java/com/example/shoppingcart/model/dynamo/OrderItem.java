package com.example.shoppingcart.model.dynamo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class OrderItem {

    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal itemPrice;
    private BigDecimal totalItemPrice;
    private String categoryHierarchy;
}
