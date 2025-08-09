package com.example.shoppingcart.repository.crud;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.util.Optional;

/**
 * Generic CRUD repository interface with basic DynamoDB implementations.
 *
 * @param <T> the type of the entity
 * @param <ID> the type of the entity's ID
 */
public interface DynamoDBCrudRepository<T, ID> {

    // Abstract methods to be implemented by concrete repository classes
    DynamoDbEnhancedClient getEnhancedClient();
    Class<T> getEntityClass();
    TableSchema<T> getTableSchema();
    DynamoDbTable<T> getTable();

    /**
     * Saves a given entity.
     *
     * @param entity the entity to save
     * @return the saved entity
     */
    default T save(T entity) {
        getTable().putItem(entity);
        return entity;
    }

    /**
     * Updates a given entity.
     *
     * @param entity the entity to update
     * @return the updated entity
     */
    default T update(T entity) {
        return getTable().updateItem(entity);
    }

    /**
     * Retrieves an entity by its ID.
     *
     * @param id the ID of the entity to retrieve
     * @return an Optional containing the entity, or empty if not found
     */
    default Optional<T> findById(ID id) {
        // This assumes the ID maps directly to the primary key. More complex key structures would need more logic.
        Key key = Key.builder().partitionValue(id.toString()).build();
        return Optional.ofNullable(getTable().getItem(key));
    }

    /**
     * Deletes an entity by its ID.
     *
     * @param id the ID of the entity to delete
     */
    default void deleteById(ID id) {
        Key key = Key.builder().partitionValue(id.toString()).build();
        getTable().deleteItem(key);
    }

    /**
     * Retrieves all entities as an iterable. This method is disabled by default to prevent full table scans.
     * Implementations may return a lazy iterable or throw {@link UnsupportedOperationException}.
     *
     * @return an iterable of all entities
     */
    default Iterable<T> findAll() {
        throw new UnsupportedOperationException("findAll() is disabled to prevent full table scans. Implement specific query methods.");
    }

    /**
     * Optional: Retrieves entities in a paginated fashion. Implementations that can support
     * pagination (for example, DynamoDB enhanced client's paging) should override this overload.
     * By default this is unsupported and will throw.
     *
     * @param pageSize maximum page size to request
     * @param lastEvaluatedKey optional last key for continuation (implementation-specific)
     * @return a PageIterable over entities
     */
    default PageIterable<T> findAll(int pageSize, String lastEvaluatedKey) {
        throw new UnsupportedOperationException("Paginated findAll is not supported by default.");
    }
}
