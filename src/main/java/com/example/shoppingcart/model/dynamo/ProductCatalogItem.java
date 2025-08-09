package com.example.shoppingcart.model.dynamo;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

/**
 * Represents a flexible item in the ProductCatalog table, implementing a single-table design.
 * This version is corrected to use the project's established and consistent annotation pattern.
 */
@NoArgsConstructor
@Data
@DynamoDbBean
public class ProductCatalogItem {

    private String productId;

    private String sk;

    private String category;

    private String name;

    private String description;

    private String imageUrl;

    private String status;

    // price stored in smallest currency unit (e.g., pence)
    private Integer priceInSmallestUnit;

    // Backwards-compatible accessors used across the codebase (some code expects getPrice()/setPrice())


    // Explicit getter annotated for DynamoDB mapping instead of Lombok's onMethod helper
    @DynamoDbAttribute("priceInSmallestUnit")
    public Integer getPriceInSmallestUnit() { return this.priceInSmallestUnit; }

    private String currency;

    private Integer stock;

    private String region;

    // Explicit getters with DynamoDB annotations to avoid Lombok onMethod compatibility issues
    @DynamoDbPartitionKey
    public String getProductId() { return productId; }

    @DynamoDbSortKey
    public String getSk() { return sk; }

    @DynamoDbSecondaryPartitionKey(indexNames = "CategoryIndex")
    @DynamoDbAttribute("category")
    public String getCategory() { return category; }

    @DynamoDbAttribute("name")
    public String getName() { return name; }

    @DynamoDbAttribute("description")
    public String getDescription() { return description; }

    @DynamoDbAttribute("imageUrl")
    public String getImageUrl() { return imageUrl; }

    @DynamoDbAttribute("status")
    public String getStatus() { return status; }

    // Lombok will generate getPriceInSmallestUnit() with the DynamoDbAttribute annotation above.

    @DynamoDbAttribute("currency")
    public String getCurrency() { return currency; }

    @DynamoDbAttribute("stock")
    public Integer getStock() { return stock; }

    @DynamoDbAttribute("region")
    public String getRegion() { return region; }
}