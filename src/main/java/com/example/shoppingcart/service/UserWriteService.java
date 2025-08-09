package com.example.shoppingcart.service;

import com.example.shoppingcart.model.postgres.User;

public interface UserWriteService {
    User registerUser(User user);
    User updateUser(User user);
    void deleteUser(Long id);
    User findByUsername(String username);
}