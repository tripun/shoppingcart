package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.UserDto;
import com.example.shoppingcart.model.dynamo.User;
import com.example.shoppingcart.service.crud.CrudService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface UserService extends CrudService<User, String, UserDto>, UserDetailsService {
    User registerUser(String username, String password);
    String login(String username, String password);
    void logout(String token);
    void createDefaultAdminIfNotExists();
    Optional<User> getUserByUsername(String username);
    void deleteUser(String username);
}