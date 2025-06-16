package org.ncp.bookapi.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    // Secret key for signing JWT (secure and private in production)
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Token validity (e.g., 1 hour)
    private final long validityInMs = 3600000;

    /**
     * Generate a JWT token for the specified username
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)
                .compact();
    }

    /**
     * Extract the username (subject) from the JWT token
     */
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Validate the token (check username matches and token is not expired)
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Check if the token has expired
     */
    private boolean isTokenExpired(String token) {
        final Date expiration = getClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    /**
     * Parse the token and extract claims
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
