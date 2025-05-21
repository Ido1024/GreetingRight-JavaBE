package org.example.greetingright.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.greetingright.entity.RefreshToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// Utility class for creating, validating, and extracting info from JWT tokens
@Component
public class JwtUtil {

    private final Key key; // Secret key for signing JWTs
    private final CustomUserDetailsService customUserDetailsService; // Used to load user details
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    public JwtUtil(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;

        // Generate a new secure key for HMAC-SHA256
        SecretKey generatedKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        this.key = generatedKey;

        String encodedKey = Base64.getUrlEncoder().encodeToString(generatedKey.getEncoded());
        logger.info("JwtUtil - Generated Secret Key (Base64 URL-safe): {}", encodedKey);
    }

    private Key getKey() {
        return this.key;
    }

    // Generates a JWT access token for the given user details
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        logger.info("JwtUtil - Generating Access Token with Key: {}", getKey());

        return Jwts.builder()
                .claims() // Start claims section
                .add(claims) // Add custom claims
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .and()
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .signWith(getKey(), SignatureAlgorithm.HS256) // Sign the token with the secret key
                .compact(); // Build the JWT string
    }

    // Generates a JWT refresh token for the given refresh token entity
    public String generateRefreshToken(RefreshToken refreshToken) {
        Map<String, Object> claims = new HashMap<>();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(refreshToken.getUser().getUsername());

        logger.info("JwtUtil - Generating Refresh Token with Key: {}", getKey());

        return Jwts.builder()
                .claims() // Start claims section
                .add(claims) // Add custom claims
                .subject(refreshToken.getUser().getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(Date.from(refreshToken.getExpiryDate()))
                .and()
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .signWith(getKey(), SignatureAlgorithm.HS256) // Sign the token with the secret key
                .compact(); // Build the JWT string
    }

    // Validates the JWT token against the user details
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token); // Extract username from token
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); // Check username and expiration
        } catch (Exception e) {
            throw new RuntimeException("The token signature is invalid: " + e.getMessage()); // Throw if invalid
        }
    }

    // Extracts the username (subject) from the JWT token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extracts a specific claim from the JWT token using a resolver function
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims); // Apply resolver to get specific claim
    }

    // Parses the JWT token and returns all claims
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getKey()) // Use the secret key to verify the token
                .build()
                .parseSignedClaims(token) // Parse the token
                .getPayload(); // Get the claims payload
    }

    // Checks if the token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extracts the expiration date from the token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}