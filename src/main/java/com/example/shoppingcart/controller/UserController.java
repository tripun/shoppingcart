package com.example.shoppingcart.controller;

import com.example.shoppingcart.dto.LoginRequest;
import com.example.shoppingcart.dto.LoginResponse;
import com.example.shoppingcart.dto.UserDto;
import com.example.shoppingcart.model.dynamo.User;
import com.example.shoppingcart.service.UserAuthenticationService;
import com.example.shoppingcart.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User and Authentication Management", description = "Endpoints for user registration, authentication, and management")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserAuthenticationService userAuthService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data or username already exists")
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserDto userDto) {
        log.info("Registering new user: {}", userDto.getUsername());
        User registeredUser = userService.registerUser(userDto.getUsername(), userDto.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user and get JWT token")
    @ApiResponse(responseCode = "200", description = "Login successful, returns JWT token")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("User login attempt: {}", loginRequest.getUsername());
        LoginResponse response = userAuthService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user (client-side token deletion)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Logout successful")
    public ResponseEntity<Void> logout() {
        log.info("User logout request received");
        // For stateless JWT, logout is primarily a client-side responsibility (deleting the token).
        // A server-side implementation would require a token blocklist.
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get user details by username")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.principal.username")
    @ApiResponse(responseCode = "200", description = "User details retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<User> getUserByUsername(@Parameter(description = "Username of the user", example = "john.doe") @PathVariable String username) {
        log.debug("Retrieving user: {}", username);
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Delete a user by username")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@Parameter(description = "Username of the user to delete", example = "john.doe") @PathVariable String username) {
        log.info("Deleting user: {}", username);
        userService.deleteUser(username);
    }
}
