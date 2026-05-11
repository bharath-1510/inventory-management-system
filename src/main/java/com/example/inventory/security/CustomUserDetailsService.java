package com.example.inventory.security;

import com.example.inventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 * Spring Security uses this service during the authentication process to load 
 * user-specific data from our database using the provided username (which is email in our case).
 */
@Service // Registers this as a Spring Service Bean
@RequiredArgsConstructor // Automatically injects the UserRepository
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user's details by their username (email).
     * Called automatically by Spring Security's AuthenticationManager during login.
     * 
     * @param username The email of the user trying to log in
     * @return UserDetails The User entity (which implements UserDetails)
     * @throws UsernameNotFoundException If the email is not in the database
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // We use the email as the username
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }
}
