package com.example.shoppingcart.repository;

import com.example.shoppingcart.model.dynamo.User; // Corrected import
import com.example.shoppingcart.repository.crud.DynamoDBCrudRepository;

import java.util.Optional;

public interface UserRepository extends DynamoDBCrudRepository<User, String> {
    Optional<User> findByUsername(String username);
}
