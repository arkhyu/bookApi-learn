package org.ncp.bookapi.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserDetailsServiceConfig {


    /**
     * Defines an in-memory user store with a single test user.
     * Equivalent to adding a test user in-memory in ASP.NET Core Identity for development/testing.
     * Password is hashed using the PasswordEncoder defined above.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        var user = org.springframework.security.core.userdetails.User.builder()
                .username("testuser")
                .password(encoder.encode("testpass"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}