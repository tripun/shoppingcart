package com.example.shoppingcart.exception;

import java.util.Arrays;

/**
 * Base exception class for all shopping cart related exceptions.
 * Provides standardized error handling with error codes and messages.
 */
public class ShoppingCartException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] args;

    public ShoppingCartException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = null;
    }

    public ShoppingCartException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.args = null;
    }

    public ShoppingCartException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = args != null ? Arrays.copyOf(args, args.length) : null;
    }

    public ShoppingCartException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.args = null;
    }

    public ShoppingCartException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.errorCode = errorCode;
        this.args = null;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Object[] getArgs() {
        return args != null ? Arrays.copyOf(args, args.length) : null;
    }
}