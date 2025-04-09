package org.example.greetingright.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.greetingright.entity.RefreshToken;
import org.example.greetingright.entity.User;
import org.example.greetingright.repository.RefreshTokenRepository;
import org.example.greetingright.repository.UserRepository;
import org.example.greetingright.security.CustomUserDetailsService;
import org.example.greetingright.security.JwtProperties;
import org.example.greetingright.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RefreshTokenService {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class); // Add Logger

    @Transactional
    public RefreshToken createRefreshToken(String username, String ipAddress){
        Optional<User> user = userRepository.findByUsername(username);
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user.orElse(null)).orElseGet(RefreshToken::new);
        refreshToken.setUser(user.orElse(null));
        refreshToken.setExpiryDate(Instant.now().plusMillis(JwtProperties.REFRESH_EXPIRATION_TIME));
        refreshToken.setIpAddress(ipAddress);
        logger.info("RefreshTokenService - Creating Refresh Token for user: {}, IP: {}", username, ipAddress); // Log refresh token creation
        refreshToken.setToken(jwtUtil.generateRefreshToken(refreshToken));
        return refreshTokenRepository.save(refreshToken);
    }
    public String refreshAccessToken(String refreshToken, String ipAddress) {
        RefreshToken validRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        // Check IP address match
        if (!validRefreshToken.getIpAddress().equals(ipAddress)) {
            throw new RuntimeException("IP address mismatch");
        }

        String username = validRefreshToken.getUser().getUsername();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        logger.info("RefreshTokenService - Refreshing Access Token for user: {}", username); // Log access token refresh
        return jwtUtil.generateToken(userDetails); // Generate new access token
    }
    @Transactional
    public void invalidateRefreshToken(String token) {
        logger.info("RefreshTokenService - Invalidating Refresh Token: {}", token);
        refreshTokenRepository.deleteByToken(token);
    }
}