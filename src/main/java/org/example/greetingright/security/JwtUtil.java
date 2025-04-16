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

@Component
public class JwtUtil {

    private final Key key;  // HMAC-SHA256 key
    private final CustomUserDetailsService customUserDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    public JwtUtil(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;

        // Generate a new secure key
        SecretKey generatedKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        this.key = generatedKey;

        // Print Base64 encoded key so it can be reused if needed
        String encodedKey = Base64.getUrlEncoder().encodeToString(generatedKey.getEncoded());
        logger.info("JwtUtil - Generated Secret Key (Base64 URL-safe): {}", encodedKey);
    }

    private Key getKey() {
        return this.key;
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        logger.info("JwtUtil - Generating Access Token with Key: {}", getKey());

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .and()
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(RefreshToken refreshToken) {
        Map<String, Object> claims = new HashMap<>();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(refreshToken.getUser().getUsername());

        logger.info("JwtUtil - Generating Refresh Token with Key: {}", getKey());

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(refreshToken.getUser().getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(Date.from(refreshToken.getExpiryDate()))
                .and()
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            throw new RuntimeException("The token signature is invalid: " + e.getMessage());
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
