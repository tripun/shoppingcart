package com.example.shoppingcart.repository.impl.nosql;

import com.example.shoppingcart.model.dynamo.User;
import com.example.shoppingcart.repository.UserRepository;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;

@Repository("userRepository")
public class UserRepositoryImpl implements UserRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<User> userTable;
    private final Class<User> entityClass;
    private final TableSchema<User> tableSchema;

    public UserRepositoryImpl(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
        this.entityClass = User.class;
        this.tableSchema = TableSchema.fromBean(entityClass);
        this.userTable = enhancedClient.table(entityClass.getSimpleName(), tableSchema);
    }

    @Override
    public DynamoDbEnhancedClient getEnhancedClient() {
        return enhancedClient;
    }

    @Override
    public Class<User> getEntityClass() {
        return entityClass;
    }

    @Override
    public TableSchema<User> getTableSchema() {
        return tableSchema;
    }

    @Override
    public DynamoDbTable<User> getTable() {
        return userTable;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return findById(username); // Use findById from DynamoDBCrudRepository
    }
}