package com.example.shoppingcart.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // Cart errors (001 to 099)
    CART_001_NOT_FOUND(HttpStatus.NOT_FOUND, "CART_001", "Cart not found"),
    CART_002_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "CART_002", "Cart item not found"),
    CART_003_MAX_ITEMS_EXCEEDED(HttpStatus.BAD_REQUEST, "CART_003", "Cart cannot contain more than 100 items"),
    CART_004_INVALID_STATE(HttpStatus.BAD_REQUEST, "CART_004", "Invalid cart state"),
    CART_005_IS_EMPTY(HttpStatus.BAD_REQUEST, "CART_005", "Shopping cart is empty"),

    // Product errors (100 to 199)
    PROD_100_NOT_FOUND(HttpStatus.NOT_FOUND, "PROD_100", "Product not found"),
    PROD_101_INVALID_ID_FORMAT(HttpStatus.BAD_REQUEST, "PROD_101", "Invalid product ID format"),
    PROD_102_ID_TOO_LONG(HttpStatus.BAD_REQUEST, "PROD_102", "Product ID must not exceed 8 characters"),
    PROD_103_INSUFFICIENT_STOCK(HttpStatus.CONFLICT, "PROD_103", "Insufficient inventory"),

    // Price errors (200 to 299)
    PRICE_200_MUST_BE_POSITIVE(HttpStatus.BAD_REQUEST, "PRICE_200", "Price must be greater than zero"),
    PRICE_201_CALCULATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PRICE_201", "Error calculating price"),
    PRICE_202_INVALID_DISCOUNT(HttpStatus.BAD_REQUEST, "PRICE_202", "Invalid discount configuration"),

    // Validation errors (300 to 399)
    VAL_300_INVALID_PARAMS(HttpStatus.BAD_REQUEST, "VAL_300", "Invalid request parameters"),
    VAL_301_INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "VAL_301", "Invalid quantity"),
    VAL_302_INVALID_REGION(HttpStatus.BAD_REQUEST, "VAL_302", "Invalid region code"),
    VAL_303_INVALID_CURRENCY(HttpStatus.BAD_REQUEST, "VAL_303", "Invalid currency code"),

    // Authentication errors (400 to 499)
    AUTH_400_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_400", "Unauthorized access"),
    AUTH_401_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_401", "Invalid or expired token"),
    AUTH_402_ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_402", "Access denied"),
    AUTH_403_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_403", "Invalid username or password"),

    // System errors (500 to 599)
    SYS_500_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SYS_500", "Internal server error"),
    SYS_501_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "SYS_501", "Service unavailable"),
    SYS_502_DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SYS_502", "Database error");

    // Backwards-compatible aliases used by older code
    public static final ErrorCode PRODUCT_INSUFFICIENT_STOCK = PROD_103_INSUFFICIENT_STOCK;
    public static final ErrorCode INVALID_INPUT = VAL_300_INVALID_PARAMS;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
