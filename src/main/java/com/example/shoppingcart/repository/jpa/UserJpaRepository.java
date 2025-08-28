package com.example.shoppingcart.repository.jpa;

import com.example.shoppingcart.model.postgres.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}