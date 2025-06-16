package org.ncp.bookapi.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration  // Marks this class as a source of bean definitions, like Startup.cs ConfigureServices
@EnableMethodSecurity  // Enables method-level security annotations (like [Authorize] on methods/controllers)
public class SecurityConfig {

    /**
     * Configures the HTTP security rules.
     * Similar to configuring the middleware pipeline in .NET Core's Configure method.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection since this is a stateless REST API (no cookie-based sessions)
                .csrf(csrf -> csrf.disable())

                // Disable default form login, because authentication will be via API tokens or similar
                .formLogin(formLogin -> formLogin.disable())

                // Define route authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Allow unauthenticated access to these API paths (like [AllowAnonymous])
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/books", "/api/books/**").permitAll()

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )

                // Set session management to stateless, like using JWT tokens without server session state
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Build and return the configured filter chain (middleware pipeline)
        return http.build();
    }

    /**
     * Defines the password encoder bean.
     * Uses BCrypt hashing algorithm, similar to ASP.NET Core's PasswordHasher<TUser>.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the AuthenticationManager bean.
     * This is responsible for processing authentication requests, like SignInManager or middleware in .NET.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Defines an in-memory user store with a single test user.
     * Equivalent to adding a test user in-memory in ASP.NET Core Identity for development/testing.
     * Password is hashed using the PasswordEncoder defined above.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        var user = org.springframework.security.core.userdetails.User.builder()
                .username("testuser")                  // Username for login
                .password(encoder.encode("testpass")) // Password hashed with BCrypt
                .roles("USER")                         // Assigns USER role
                .build();

        // In-memory user details manager stores this user for authentication purposes
        return new org.springframework.security.provisioning.InMemoryUserDetailsManager(user);
    }
}
