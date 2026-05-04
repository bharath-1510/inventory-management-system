package com.example.inventory.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A filter that intercepts every incoming HTTP request to log its details and the time taken to process it.
 */
@Component // Registers this filter as a Spring Bean so it is automatically applied to requests
@Slf4j // Lombok annotation for automatic logging (creates a 'log' instance)
public class LoggingFilter extends OncePerRequestFilter { // Ensures the filter is executed exactly once per request

    /**
     * Intercepts the request, logs the start, passes it down the filter chain, and logs the response/duration.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis(); // Track when the request started
        log.info("Incoming Request: {} {}", request.getMethod(), request.getRequestURI());

        try {
            // Continue processing the request (passes it to the controller, etc.)
            filterChain.doFilter(request, response);
        } finally {
            // The finally block ensures this runs even if an exception is thrown during processing
            long duration = System.currentTimeMillis() - startTime;
            log.info("Outgoing Response: {} {} - Status: {} - Time Taken: {} ms",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), duration);
        }
    }
}
