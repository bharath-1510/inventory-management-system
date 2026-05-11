package com.example.inventory.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Global exception handler that intercepts exceptions thrown anywhere in the application
 * and formats them into a standard JSON ErrorResponse.
 */
@Slf4j // Lombok annotation for automatic logging
@RestControllerAdvice // Combines @ControllerAdvice and @ResponseBody. Catches exceptions and returns JSON responses.
public class GlobalExceptionHandler {

    /**
     * Intercepts validation errors (like @NotNull, @Min) thrown by @Valid payloads.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class) // Specifies which exception this method handles
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        // Extract and combine all validation error messages into a single string
        String errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.error("Validation Error caught: {}", errorMessages);

        ErrorResponse errorResponse = new ErrorResponse(
                "Validation Error",
                errorMessages,
                HttpStatus.BAD_REQUEST.value(), // 400 status code for bad requests
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Catch-all handler for unexpected generic Exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Internal Server Error caught: ", ex);
        ErrorResponse errorResponse = new ErrorResponse(
                "Internal Server Error",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(), // 500 status code
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Intercepts application-specific runtime exceptions (e.g., custom business logic errors).
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        log.error("Application Runtime Error caught: ", ex);
        ErrorResponse errorResponse = new ErrorResponse(
                "Application Error",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(), // Usually mapped to 400 Bad Request
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles Authentication errors (e.g., bad credentials, missing token).
     */
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(org.springframework.security.core.AuthenticationException ex, WebRequest request) {
        log.error("Authentication Error caught: ", ex);
        ErrorResponse errorResponse = new ErrorResponse(
                "Unauthorized",
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles Access Denied errors (e.g., trying to access an ADMIN route as a CUSTOMER).
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex, WebRequest request) {
        log.error("Access Denied Error caught: ", ex);
        ErrorResponse errorResponse = new ErrorResponse(
                "Forbidden",
                "You do not have permission to access this resource",
                HttpStatus.FORBIDDEN.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles specific JWT parsing/validation errors explicitly.
     */
    @ExceptionHandler({io.jsonwebtoken.ExpiredJwtException.class, io.jsonwebtoken.MalformedJwtException.class, io.jsonwebtoken.security.SignatureException.class, io.jsonwebtoken.JwtException.class})
    public ResponseEntity<ErrorResponse> handleJwtExceptions(Exception ex, WebRequest request) {
        log.error("JWT Error caught: ", ex);
        String message = "Invalid or expired token";
        if (ex instanceof io.jsonwebtoken.ExpiredJwtException) {
            message = "Token has expired. Please log in again.";
        } else if (ex instanceof io.jsonwebtoken.security.SignatureException) {
            message = "Token signature is invalid.";
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Unauthorized",
                message,
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}
