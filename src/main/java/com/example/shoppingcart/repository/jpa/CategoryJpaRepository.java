package com.example.shoppingcart.repository.jpa;

import com.example.shoppingcart.model.postgres.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryJpaRepository extends JpaRepository<Category, String> {
}
