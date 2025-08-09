package com.example.shoppingcart.repository.impl.dynamo;

import com.example.shoppingcart.model.dynamo.InventoryRecord;
import com.example.shoppingcart.repository.InventoryReadRepository;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;

@Repository
public class InventoryReadRepositoryImpl implements InventoryReadRepository {

    private final DynamoDbTable<InventoryRecord> inventoryTable;

    public InventoryReadRepositoryImpl(DynamoDbEnhancedClient enhancedClient) {
        this.inventoryTable = enhancedClient.table("Inventory", TableSchema.fromBean(InventoryRecord.class));
    }

    @Override
    public Optional<InventoryRecord> findById(String productId, String region) {
        // Use a composite partition value convention: PRODUCT#<productId>#<region>
        String partition = "PRODUCT#" + productId + (region != null ? "#" + region : "");
        Key key = Key.builder().partitionValue(partition).build();
        return Optional.ofNullable(inventoryTable.getItem(key));
    }
}
