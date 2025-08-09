package com.example.shoppingcart.model.dynamo;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Data
@DynamoDbBean
public class CatalogItem {
    private String pk;
    private String sk;
    private String name;
    private Integer price;
    private String currency;
    private String categoryHierarchy;
    private String regionHierarchy;
    private String description;
    private String status;
    private String imageUrl;
    private Integer stock;
    private String region;

    @DynamoDbPartitionKey
    public String getPk() { return pk; }

    @DynamoDbSortKey
    public String getSk() { return sk; }

    // Lombok's @Data provides setters; explicit setters removed.
}
