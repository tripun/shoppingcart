package com.example.shoppingcart.repository.jpa;

import com.example.shoppingcart.model.postgres.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, String> {
}
