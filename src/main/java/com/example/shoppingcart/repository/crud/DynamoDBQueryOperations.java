package com.example.shoppingcart.repository.crud;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;

import java.util.function.Consumer;

public interface DynamoDBQueryOperations {

    static <T> PageIterable<T> query(DynamoDbEnhancedClient enhancedClient, Class<T> entityClass, QueryConditional queryConditional) {
        DynamoDbTable<T> table = enhancedClient.table(entityClass.getSimpleName(), TableSchema.fromBean(entityClass));
        return table.query(queryConditional);
    }

    static <T> PageIterable<T> query(DynamoDbEnhancedClient enhancedClient, Class<T> entityClass, QueryEnhancedRequest request) {
        DynamoDbTable<T> table = enhancedClient.table(entityClass.getSimpleName(), TableSchema.fromBean(entityClass));
        return table.query(request);
    }

    static <T> T updateComplex(DynamoDbEnhancedClient enhancedClient, Class<T> entityClass, T item, Consumer<UpdateItemEnhancedRequest.Builder<T>> requestConsumer) {
        DynamoDbTable<T> table = enhancedClient.table(entityClass.getSimpleName(), TableSchema.fromBean(entityClass));
        UpdateItemEnhancedRequest.Builder<T> requestBuilder = UpdateItemEnhancedRequest.builder(entityClass).item(item);
        requestConsumer.accept(requestBuilder);
        return table.updateItem(requestBuilder.build());
    }
}
