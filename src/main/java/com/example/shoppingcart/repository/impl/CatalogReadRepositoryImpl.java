package com.example.shoppingcart.repository.impl;

import com.example.shoppingcart.model.dynamo.CatalogItem;
import com.example.shoppingcart.repository.CatalogReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CatalogReadRepositoryImpl implements CatalogReadRepository {

    private final DynamoDbTable<CatalogItem> catalogTable;
    private final DynamoDbIndex<CatalogItem> categoryIndex; // GSI for category lookups

    @Autowired
    public CatalogReadRepositoryImpl(DynamoDbEnhancedClient enhancedClient) {
        this.catalogTable = enhancedClient.table("Catalog", TableSchema.fromBean(CatalogItem.class));
        // This assumes a GSI named 'category-index' exists on the table.
        // The GSI partition key would be a composite key like 'REGION#CATEGORY'
        this.categoryIndex = catalogTable.index("category-index");
    }

    @Override
    public Optional<CatalogItem> findById(String productId) {
        // We perform a partition-only lookup; the table design uses PRODUCT#<id> as PK
        Key key = Key.builder().partitionValue("PRODUCT#" + productId).build();
        return Optional.ofNullable(catalogTable.getItem(key));
    }

    @Override
    public List<CatalogItem> findByCategory(String categoryHierarchy, int limit) {
        // Query the GSI using categoryHierarchy as the partition key (assumes index exists)
        QueryConditional qc = QueryConditional.keyEqualTo(k -> k.partitionValue(categoryHierarchy));
        QueryEnhancedRequest request = QueryEnhancedRequest.builder().queryConditional(qc).limit(limit).build();

        return categoryIndex.query(request).stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }
}
