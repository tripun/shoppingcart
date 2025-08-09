package com.example.shoppingcart.repository.dynamo;

import com.example.shoppingcart.model.dynamo.OrderItem;
import com.example.shoppingcart.repository.crud.DynamoDBCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends DynamoDBCrudRepository<OrderItem, String> {
}
