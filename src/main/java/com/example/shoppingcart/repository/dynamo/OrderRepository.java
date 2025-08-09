package com.example.shoppingcart.repository.dynamo;

import com.example.shoppingcart.model.dynamo.Order;
import com.example.shoppingcart.repository.crud.DynamoDBCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends DynamoDBCrudRepository<Order, String> {
}
