package com.example.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for monitoring the application's health.
 * Commonly used by load balancers or orchestrators (like Kubernetes) to check if the app is alive.
 */
@RestController // Marks this class as a REST controller
@RequestMapping("/api") // Base URI for this controller
public class HealthCheckController {

    @Autowired // Instructs Spring to inject the JdbcTemplate bean automatically
    private JdbcTemplate jdbcTemplate; // Used for executing raw SQL queries

    /**
     * Endpoint to check the application's status and database connectivity.
     */
    @GetMapping("/health") // Handles GET requests to "/api/health"
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP"); // Indicate the Spring Boot application itself is running

        try {
            // Attempt a simple query to verify the database connection is active
            jdbcTemplate.execute("SELECT 1");
            response.put("database", "CONNECTED");
            return ResponseEntity.ok(response); // Return HTTP 200 OK
        } catch (Exception e) {
            // If the query fails, the database is unreachable
            response.put("database", "DISCONNECTED");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response); // Return HTTP 503
        }
    }
}
