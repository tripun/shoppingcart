package com.example.shoppingcart.controller;

import com.example.shoppingcart.dto.LoginRequest;
import com.example.shoppingcart.dto.LoginResponse;
import com.example.shoppingcart.service.UserAuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserAuthenticationService userAuthService;

    @Autowired
    public AuthController(UserAuthenticationService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @Operation(summary = "Authenticate user and receive a JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful, JWT returned"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials provided")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = userAuthService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}
