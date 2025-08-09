package com.example.shoppingcart.repository.dynamo;

import com.example.shoppingcart.model.dynamo.AppliedDiscount;
import com.example.shoppingcart.repository.crud.DynamoDBCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppliedDiscountRepository extends DynamoDBCrudRepository<AppliedDiscount, String> {
}
