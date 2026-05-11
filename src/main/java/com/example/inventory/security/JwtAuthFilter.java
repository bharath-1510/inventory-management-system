package com.example.inventory.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A custom filter that runs once per HTTP request.
 * It checks the "Authorization" header for a valid JWT token, extracts the user details,
 * and sets the authenticated user in the Spring Security context.
 */
@Component // Registers this filter as a Spring Bean
@RequiredArgsConstructor // Automatically creates a constructor to inject final dependencies
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService; // Helper to extract and validate tokens
    private final UserDetailsService userDetailsService; // Service to load user records from DB
    private final org.springframework.web.servlet.HandlerExceptionResolver handlerExceptionResolver;

    /**
     * Intercepts incoming requests to validate JWT tokens.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization"); // Grab the Authorization header
        final String jwt;
        final String userEmail;

        // If no Authorization header or it doesn't start with "Bearer ", ignore and let it pass through
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the actual token (skip the "Bearer " prefix)
        jwt = authHeader.substring(7);

        try {
            // Extract the email embedded inside the JWT payload
            userEmail = jwtService.extractUsername(jwt);

            // If we found an email, but the user is not yet authenticated in the current security context
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Fetch the user from the database
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // Verify that the token actually belongs to this user and isn't expired
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Create an authentication object using the UserDetails and their roles
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                            
                    // Attach extra request details (like IP address, session ID, etc.)
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                            
                    // Save the authentication object to the Security Context.
                    // Spring now knows exactly who is making this request!
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token is invalid, expired, or tampered with.
            // Instead of just logging it, we delegate the exception to the HandlerExceptionResolver
            // so it can be picked up by our GlobalExceptionHandler and returned as a nice JSON response.
            logger.error("Could not set user authentication in security context", e);
            handlerExceptionResolver.resolveException(request, response, null, e);
            return; // Stop filter chain execution since we encountered an error
        }

        // Continue processing the request down the filter chain
        filterChain.doFilter(request, response);
    }
}
