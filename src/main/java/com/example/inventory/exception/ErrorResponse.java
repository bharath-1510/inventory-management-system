package com.example.inventory.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A standard Data Transfer Object (DTO) used to return a consistent JSON structure
 * to the client whenever an error occurs in the application.
 */
@Data // Lombok annotation to automatically generate getters, setters, toString, equals, and hashCode
@AllArgsConstructor // Generates a constructor with all fields
@NoArgsConstructor // Generates a no-argument constructor (needed for JSON deserialization)
public class ErrorResponse {
    
    // A short, human-readable summary of the error type (e.g., "Validation Error")
    private String heading;
    
    // Detailed message explaining what went wrong (e.g., "Product name is required")
    private String message;
    
    // The HTTP status code returned (e.g., 400, 500)
    private int status;
    
    // The exact time the error occurred
    private LocalDateTime timestamp;
}
