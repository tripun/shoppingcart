package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.LoginRequest;
import com.example.shoppingcart.dto.LoginResponse;
import com.example.shoppingcart.exception.ErrorCode;
import com.example.shoppingcart.exception.ShoppingCartException;
import com.example.shoppingcart.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class UserAuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserAuthenticationService(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            String token = jwtTokenProvider.createToken(authentication);
            return new LoginResponse(token);
        } catch (AuthenticationException e) {
            // Use the most specific error code for this failure.
            throw new ShoppingCartException(ErrorCode.AUTH_403_INVALID_CREDENTIALS);
        }
    }
}
