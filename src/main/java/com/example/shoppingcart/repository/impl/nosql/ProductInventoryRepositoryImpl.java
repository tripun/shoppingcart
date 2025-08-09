package com.example.shoppingcart.repository.impl.nosql;

import com.example.shoppingcart.model.dynamo.InventoryRecord;
import com.example.shoppingcart.model.dynamo.ProductCatalogItem;
import com.example.shoppingcart.model.ProductInventory;
import com.example.shoppingcart.repository.ProductCatalogRepository;
import com.example.shoppingcart.repository.ProductInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

/**
 * NoSQL implementation of the ProductInventoryRepository that acts as a facade over the single-table ProductCatalogRepository.
 * It translates between the ProductInventory model and the persistence-focused ProductCatalogItem model.
 */
@Repository("productInventoryRepository")
@RequiredArgsConstructor
public class ProductInventoryRepositoryImpl implements ProductInventoryRepository {

    private final ProductCatalogRepository productCatalogRepository;
    private static final String INVENTORY_SK_PREFIX = "INVENTORY#";
    private final DynamoDbTable<InventoryRecord> inventoryTable;

    // If an enhanced client is available we can return it, otherwise facade callers will hit UnsupportedOperationException
    private final DynamoDbEnhancedClient enhancedClient;

    @Override
    public <S extends ProductInventory> S save(S inventory) {
        ProductCatalogItem item = toCatalogItem(inventory);
        productCatalogRepository.save(item);
        return inventory;
    }

    @Override
    public Optional<ProductInventory> findById(String productId, String region) {
        String sk = INVENTORY_SK_PREFIX + region;
        ProductCatalogItem item = productCatalogRepository.findByProductIdAndSk(productId, sk);
        return Optional.ofNullable(item).map(this::toProductInventory);
    }

    public void deleteById(String productId, String region) {
        findById(productId, region).ifPresent(this::delete);
    }

    @Override
    public void delete(ProductInventory inventory) {
        ProductCatalogItem item = toCatalogItem(inventory);
        productCatalogRepository.delete(item);
    }

    // --- Helper Methods for Data Mapping ---

    private ProductInventory toProductInventory(ProductCatalogItem item) {
        ProductInventory inventory = new ProductInventory();
        inventory.setProductId(item.getProductId());
        inventory.setRegion(item.getRegion());
        inventory.setQuantity(item.getStock());
        return inventory;
    }

    private ProductCatalogItem toCatalogItem(ProductInventory inventory) {
        ProductCatalogItem item = new ProductCatalogItem();
        item.setProductId(inventory.getProductId());
        item.setSk(INVENTORY_SK_PREFIX + inventory.getRegion());
        item.setRegion(inventory.getRegion());
        item.setStock(inventory.getQuantity());
        return item;
    }

    // Provide DynamoDB helper methods (facade-only)
    public DynamoDbTable<InventoryRecord> getTable() {
        return inventoryTable;
    }

    public DynamoDbEnhancedClient getEnhancedClient() {
        if (enhancedClient == null) throw new UnsupportedOperationException("Not available from facade repository");
        return enhancedClient;
    }

    public Class<InventoryRecord> getEntityClass() {
        return InventoryRecord.class;
    }

    public TableSchema<InventoryRecord> getTableSchema() {
        return TableSchema.fromBean(InventoryRecord.class);
    }

    // --- Unsupported Operations from CrudRepository ---

    // Implementations of the DynamoDBCrudRepository methods for the underlying InventoryRecord entity.
    public Optional<com.example.shoppingcart.model.dynamo.InventoryRecord> findById(String s) {
        throw new UnsupportedOperationException("Use findById(productId, region)");
    }
    public boolean existsById(String s) {
        throw new UnsupportedOperationException("Use findById(productId, region)");
    }
    public Iterable<com.example.shoppingcart.model.dynamo.InventoryRecord> findAll() {
        throw new UnsupportedOperationException("findAll is not supported for inventory. Query by product.");
    }
    public Iterable<com.example.shoppingcart.model.dynamo.InventoryRecord> findAllById(Iterable<String> strings) {
        throw new UnsupportedOperationException("findAllById is not supported for inventory.");
    }
    public long count() {
        throw new UnsupportedOperationException("count is not supported for inventory.");
    }
    public void deleteById(String s) {
        throw new UnsupportedOperationException("Use deleteById(productId, region)");
    }
    public void deleteAll(Iterable<? extends com.example.shoppingcart.model.dynamo.InventoryRecord> entities) {
        throw new UnsupportedOperationException("deleteAll by InventoryRecord is not supported.");
    }
    public void deleteAll() {
        throw new UnsupportedOperationException("deleteAll is not supported for inventory.");
    }
}
