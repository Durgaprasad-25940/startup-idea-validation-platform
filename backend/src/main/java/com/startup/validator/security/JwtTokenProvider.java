package com.startup.validator.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JwtTokenProvider handles the creation, parsing, and validation of JSON Web Tokens.
 * 
 * Explanation of Annotations and Methods:
 * `@Component`: Makes this class a Spring-managed bean.
 * `@Value`: Inject properties from the `application.properties` configuration file.
 * `generateToken()`: Creates a signed JWT with the user's username (subject), current time (issuedAt), expiration date, and HMAC SHA algorithm.
 * `getUsername()`: Parses a validated JWT to extract the subject (username).
 * `validateToken()`: Validates that the token signature is correct and has not expired.
 */
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-milliseconds}")
    private long jwtExpirationDate;

    /**
     * Generate a new signed JWT token based on the user authentication details.
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .subject(username)
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(key())
                .compact();
    }

    /**
     * Decode and return the secret signing key.
     */
    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Extract the username from a JWT.
     */
    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Verify that the JWT is valid and authentic.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // Can be expanded to capture specific JWT exceptions (ExpiredJwtException, MalformedJwtException, SignatureException)
            return false;
        }
    }
}
