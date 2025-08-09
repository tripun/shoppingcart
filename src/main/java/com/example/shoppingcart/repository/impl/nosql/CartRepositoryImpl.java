package com.example.shoppingcart.repository.impl.nosql;

import com.example.shoppingcart.model.dynamo.ShoppingCart;
import com.example.shoppingcart.repository.ShoppingCartRepository;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * The DynamoDB implementation of the CartRepository interface.
 * This version is corrected to be compilable and to fix a critical performance bug
 * by querying the UserCartIndex GSI instead of scanning the base table.
 */
@Repository("cartRepository")
public class CartRepositoryImpl implements ShoppingCartRepository {

    private final DynamoDbTable<ShoppingCart> cartTable;
    private final DynamoDbEnhancedClient enhancedClient;

    public CartRepositoryImpl(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
        this.cartTable = enhancedClient.table(ShoppingCart.class.getSimpleName(), TableSchema.fromBean(ShoppingCart.class));
    }

    // --- Methods from DynamoDBCrudRepository --- 
    // These are required by the base interface to power its default methods.

    public DynamoDbEnhancedClient getEnhancedClient() {
        return enhancedClient;
    }

    public Class<ShoppingCart> getEntityClass() {
        return ShoppingCart.class;
    }

    public TableSchema<ShoppingCart> getTableSchema() {
        return TableSchema.fromBean(ShoppingCart.class);
    }

    public DynamoDbTable<ShoppingCart> getTable() {
        return cartTable;
    }

    // --- Custom CartRepository Methods ---

    public Optional<ShoppingCart> findByUserId(String userId) {
        // 1. Correctly get the GSI object.
        DynamoDbIndex<ShoppingCart> userCartIndex = cartTable.index("UserCartIndex");

        // 2. Build the query against the GSI partition key.
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(userId).build());

        // 3. Execute the query on the INDEX, not the table, for maximum performance.
        return userCartIndex.query(queryConditional).stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }

    // --- Missing Required Methods from CrudRepository ---
    // These methods are added to make the class compilable.

    public <S extends ShoppingCart> S save(S entity) {
        cartTable.putItem(entity);
        return entity;
    }

    @Override
    public <S extends ShoppingCart> Iterable<S> saveAll(Iterable<S> entities) {
        entities.forEach(cartTable::putItem);
        return entities;
    }

    @Override
    public Optional<ShoppingCart> findById(String cartId) {
        return Optional.ofNullable(cartTable.getItem(Key.builder().partitionValue(cartId).build()));
    }

    @Override
    public boolean existsById(String cartId) {
        return findById(cartId).isPresent();
    }

    @Override
    public void deleteById(String cartId) {
        cartTable.deleteItem(Key.builder().partitionValue(cartId).build());
    }

    @Override
    public void delete(ShoppingCart entity) {
        deleteById(entity.getCartId());
    }

    @Override
    public void deleteAll(Iterable<? extends ShoppingCart> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAllById(Iterable<? extends String> ids) {
        ids.forEach(this::deleteById);
    }

    // --- Methods that require table scans are explicitly not supported --- 

    @Override
    public Iterable<ShoppingCart> findAll() {
        throw new UnsupportedOperationException("findAll is disabled to prevent full table scans.");
    }

    @Override
    public Iterable<ShoppingCart> findAllById(Iterable<String> strings) {
        return StreamSupport.stream(strings.spliterator(), false)
                .map(this::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("count is disabled to prevent full table scans.");
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("deleteAll is disabled to prevent full table scans.");
    }
}
