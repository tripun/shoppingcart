package com.example.shoppingcart.repository.impl.dynamo;

import com.example.shoppingcart.model.dynamo.CatalogItem;
import com.example.shoppingcart.repository.CatalogReadRepository;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CatalogReadRepositoryImpl implements CatalogReadRepository {

    private final DynamoDbTable<CatalogItem> catalogTable;

    public CatalogReadRepositoryImpl(DynamoDbEnhancedClient enhancedClient) {
        this.catalogTable = enhancedClient.table("Catalog", TableSchema.fromBean(CatalogItem.class));
    }

    @Override
    public Optional<CatalogItem> findById(String productId) {
        // Assume PK format PRODUCT#<id>; the table may use SK for region but we perform partition lookup
        Key key = Key.builder().partitionValue("PRODUCT#" + productId).build();
        return Optional.ofNullable(catalogTable.getItem(key));
    }

    @Override
    public List<CatalogItem> findByCategory(String categoryHierarchy, int limit) {
        // WARNING: This is a full table scan with a filter, which is inefficient for large tables.
        // A Global Secondary Index (GSI) on categoryHierarchy and region would be required for efficient querying.
    // Fall back to scanning and filtering in-memory when a GSI is not available.
    return catalogTable.scan().stream()
        .flatMap(page -> page.items().stream())
        .filter(item -> categoryHierarchy.equals(item.getCategoryHierarchy()))
        .limit(limit)
        .collect(Collectors.toList());
    }
}