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

@Repository
public class ShoppingCartRepositoryImpl implements ShoppingCartRepository {

    private final DynamoDbTable<ShoppingCart> cartTable;
    private final DynamoDbEnhancedClient enhancedClient;

    public ShoppingCartRepositoryImpl(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
        this.cartTable = enhancedClient.table(ShoppingCart.class.getSimpleName(), TableSchema.fromBean(ShoppingCart.class));
    }

    @Override
    public <S extends ShoppingCart> S save(S entity) {
        cartTable.putItem(entity);
        return entity;
    }

    @Override
    public <S extends ShoppingCart> Iterable<S> saveAll(Iterable<S> entities) {
        entities.forEach(this::save);
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
    public Iterable<ShoppingCart> findAll() {
        return cartTable.scan().items();
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
        return cartTable.scan().items().stream().count();
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
    public void deleteAllById(Iterable<? extends String> strings) {
        strings.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends ShoppingCart> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("deleteAll is disabled to prevent full table scans.");
    }
}
