package com.example.inventory.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility Service for interacting with JSON Web Tokens (JWT).
 * Handles generating new tokens for authenticated users, and extracting/verifying claims from incoming tokens.
 */
@Service // Registers this class as a Spring Service Bean
public class JwtService {

    // Using a static hardcoded key for demonstration. 
    // In production, this should be in application.properties and loaded via @Value.
    // This key is used to "sign" the token, ensuring it hasn't been tampered with.
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    /**
     * Extracts the Username (in our case, the email) from the JWT token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Generic method to extract a specific claim from the token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a new JWT token for a given user without any extra custom claims.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a new JWT token, embedding user details and setting the expiration date.
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims) // Add any extra data
                .setSubject(userDetails.getUsername()) // Set the subject (email)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Set creation time
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // Set expiration to 24 hours from now
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Sign it with our secret key and the HS256 algorithm
                .compact(); // Build the final string
    }

    /**
     * Verifies if a given token is valid for a specific user.
     * It checks if the token's username matches the database, and if the token is still unexpired.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Checks if the token's expiration date has passed.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Parses the JWT string and returns all claims (data) inside it.
     * If the token is invalid or tampered with, this will throw an exception.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) // Provide the key used to sign it
                .build()
                .parseClaimsJws(token) // Decode and verify the signature
                .getBody(); // Return the payload (claims)
    }

    /**
     * Converts the BASE64 string secret key into a cryptographic Key object used for signing.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
