package com.example.shoppingcart.service.impl.nosql;

import com.example.shoppingcart.dto.UserDto;
import com.example.shoppingcart.model.dynamo.User; // Corrected import
import com.example.shoppingcart.repository.UserRepository; // DynamoDB (for reads)
import com.example.shoppingcart.security.JwtTokenProvider;
import com.example.shoppingcart.service.UserService;
import com.example.shoppingcart.service.UserWriteService; // New import for write service
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, ApplicationListener<ApplicationReadyEvent> {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository; // DynamoDB (for reads)
    private final UserWriteService userWriteService; // New: for writes to PostgreSQL
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        createDefaultAdminIfNotExists();
    }

    @Override
    public User registerUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) { // Checks DynamoDB
            throw new IllegalArgumentException("Username already exists");
        }

        com.example.shoppingcart.model.postgres.User postgresUser = new com.example.shoppingcart.model.postgres.User(); // Use fully qualified name
        postgresUser.setUsername(username);
        postgresUser.setPasswordHash(passwordEncoder.encode(password));
        postgresUser.setRole("USER");
        postgresUser.setEmail(username + "@example.com");
        postgresUser.setUserCurrency("GBP");
        postgresUser.setActive(true);
        postgresUser.setDeleted(false);

        userWriteService.registerUser(postgresUser);

        User dynamoDbUser = new User();
        dynamoDbUser.setUsername(username);
        dynamoDbUser.setRole("USER");
        return dynamoDbUser;
    }

    @Override
    public String login(String username, String password) {
        UserDetails user = loadUserByUsername(username);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        String token = jwtTokenProvider.generateToken(user);
        redisTemplate.opsForValue().set(
            "token:" + token,
            username,
            jwtTokenProvider.getExpirationTime(),
            TimeUnit.MILLISECONDS
        );
        return token;
    }

    @Override
    public void logout(String token) {
        redisTemplate.delete("token:" + token);
    }

    @Override
    public void createDefaultAdminIfNotExists() {
        if (userWriteService.findByUsername("admin") == null) { // Check PostgreSQL
            com.example.shoppingcart.model.postgres.User admin = new com.example.shoppingcart.model.postgres.User();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setEmail("admin@example.com");
            admin.setUserCurrency("GBP");
            admin.setActive(true);
            admin.setDeleted(false);
            userWriteService.registerUser(admin);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Override
    public User create(UserDto dto) {
        return registerUser(dto.getUsername(), dto.getPassword());
    }

    @Override
    public Optional<User> getById(String id) {
        return getUserByUsername(id);
    }

    @Override
    public User update(String id, UserDto dto) {
        com.example.shoppingcart.model.postgres.User existingPostgresUser = userWriteService.findByUsername(id);
        if (existingPostgresUser == null) {
            throw new UsernameNotFoundException("User not found with username: " + id);
        }

        existingPostgresUser.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        existingPostgresUser.setRole(dto.getRole());
        // Assuming other fields like email, firstName, lastName, userCurrency, active, deleted are not updated via this DTO
        // If they are, they should be added to UserDto and updated here.

        userWriteService.updateUser(existingPostgresUser);

        User dynamoDbUser = new User();
        dynamoDbUser.setUsername(existingPostgresUser.getUsername());
        dynamoDbUser.setRole(existingPostgresUser.getRole());
        // Set other fields if necessary for the DynamoDB User representation
        return dynamoDbUser;
    }

    @Override
    public void delete(String id) {
        com.example.shoppingcart.model.postgres.User postgresUserToDelete = userWriteService.findByUsername(id);
        if (postgresUserToDelete != null) {
            userWriteService.deleteUser(postgresUserToDelete.getId());
            userRepository.deleteById(id); // Delete from DynamoDB as well
        } else {
            throw new UsernameNotFoundException("User not found with username: " + id);
        }
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void deleteUser(String username) {
        com.example.shoppingcart.model.postgres.User postgresUser = userWriteService.findByUsername(username);
        if (postgresUser != null) {
            userWriteService.deleteUser(postgresUser.getId());
            userRepository.findByUsername(username).ifPresent(u -> userRepository.deleteById(u.getUsername()));
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}