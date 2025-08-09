package com.example.shoppingcart.service.impl;

import com.example.shoppingcart.model.postgres.User;
import com.example.shoppingcart.repository.jpa.UserJpaRepository;
import com.example.shoppingcart.service.UserWriteService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserWriteServiceImpl implements UserWriteService {

    private static final Logger log = LoggerFactory.getLogger(UserWriteServiceImpl.class);

    private final UserJpaRepository userJpaRepository;

    @Override
    @Transactional
    public User registerUser(User user) {
        log.info("WRITING to RDBMS: Registering user: {}", user.getUsername());
        return userJpaRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        log.info("WRITING to RDBMS: Updating user with ID: {}", user.getId());
        return userJpaRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("WRITING to RDBMS: Deleting user with ID: {}", id);
        userJpaRepository.deleteById(id);
    }

    @Override
    public User findByUsername(String username) {
        log.debug("READING from RDBMS: Finding user by username: {}", username);
        return userJpaRepository.findByUsername(username);
    }
}