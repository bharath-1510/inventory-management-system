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
}
