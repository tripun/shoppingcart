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
public class AppliedDiscount {

    private String ruleId;
    private String ruleName;
    private BigDecimal amount;
    private String description;
}
