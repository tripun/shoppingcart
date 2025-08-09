package com.example.shoppingcart.exception;

import com.example.shoppingcart.dto.AppApiResponse;
import com.example.shoppingcart.exception.ErrorCode;
import com.example.shoppingcart.exception.ShoppingCartException;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /** Handles custom ShoppingCartException with proper error codes and HTTP status */
  @ExceptionHandler(ShoppingCartException.class)
  protected ResponseEntity<AppApiResponse<Object>> handleShoppingCartException(
      ShoppingCartException ex, WebRequest request) {

    log.error(
        "Shopping cart exception: {} - {}", ex.getErrorCode().getCode(), ex.getMessage(), ex);

    AppApiResponse<Object> errorResponse =
        AppApiResponse.error(ex.getMessage(), ex.getErrorCode().getCode());

    return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(errorResponse);
  }

  /** Handles validation errors from @Valid annotations */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      @NonNull MethodArgumentNotValidException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status,
      @NonNull WebRequest request) {

    Map<String, String> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .collect(
                Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage,
                    (existing, replacement) -> existing));

    log.warn("Validation failed: {}", errors);

    return createErrorResponse(
        "Validation failed: " + errors.toString(),
        HttpStatus.BAD_REQUEST,
        request,
        ErrorCode.VAL_300_INVALID_PARAMS.getCode(),
        errors);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  protected ResponseEntity<Object> handleIllegalArgument(
      IllegalArgumentException ex, WebRequest request) {
    log.warn("Invalid argument: {}", ex.getMessage());

    return createErrorResponse(
        ex.getMessage(),
        ErrorCode.VAL_300_INVALID_PARAMS.getHttpStatus(),
        request,
        ErrorCode.VAL_300_INVALID_PARAMS.getCode(),
        null);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  protected ResponseEntity<Object> handleConstraintViolation(
      ConstraintViolationException ex, WebRequest request) {
    String violations =
        ex.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.joining(", "));

    log.warn("Constraint violation: {}", violations);

    return createErrorResponse(
        "Validation failed: " + violations,
        ErrorCode.VAL_300_INVALID_PARAMS.getHttpStatus(),
        request,
        ErrorCode.VAL_300_INVALID_PARAMS.getCode(),
        null);
  }

  @ExceptionHandler(AuthenticationException.class)
  protected ResponseEntity<Object> handleAuthentication(
      AuthenticationException ex, WebRequest request) {
    log.warn("Authentication failed: {}", ex.getMessage());

    return createErrorResponse(
        "Authentication failed",
        ErrorCode.AUTH_400_UNAUTHORIZED.getHttpStatus(),
        request,
        ErrorCode.AUTH_400_UNAUTHORIZED.getCode(),
        null);
  }

  @ExceptionHandler(AccessDeniedException.class)
  protected ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
    log.warn("Access denied: {}", ex.getMessage());

    return createErrorResponse(
        "Access denied",
        ErrorCode.AUTH_402_ACCESS_DENIED.getHttpStatus(),
        request,
        ErrorCode.AUTH_402_ACCESS_DENIED.getCode(),
        null);
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<Object> handleGeneral(Exception ex, WebRequest request) {
    log.error("Unexpected error occurred", ex);

    return createErrorResponse(
        "An unexpected error occurred",
        ErrorCode.SYS_500_INTERNAL_ERROR.getHttpStatus(),
        request,
        ErrorCode.SYS_500_INTERNAL_ERROR.getCode(),
        null);
  }

  private ResponseEntity<Object> createErrorResponse(
      String message, HttpStatus status, WebRequest request, String errorCode, Object details) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());
    body.put("message", message);
    body.put("errorCode", errorCode);
    body.put("path", request.getDescription(false).replace("uri=", ""));

    if (details != null) {
      body.put("details", details);
    }

    return ResponseEntity.status(status).body(body);
  }
}