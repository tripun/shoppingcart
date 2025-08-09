package com.example.shoppingcart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API response wrapper for all REST endpoints.
 * Provides consistent response structure across the application.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    private LocalDateTime timestamp;

    public static <T> AppApiResponse<T> success(T data) {
    return new AppApiResponse<T>(true, "Operation successful", data, null, LocalDateTime.now());
    }

    public static <T> AppApiResponse<T> success(String message, T data) {
    return new AppApiResponse<T>(true, message, data, null, LocalDateTime.now());
    }

    public static <T> AppApiResponse<T> error(String message, String errorCode) {
    return new AppApiResponse<T>(false, message, null, errorCode, LocalDateTime.now());
    }

    public static <T> AppApiResponse<T> error(String message) {
    return new AppApiResponse<T>(false, message, null, null, LocalDateTime.now());
    }
}

