package org.example.greetingright.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.greetingright.entity.RefreshToken;
import org.example.greetingright.entity.User;
import org.example.greetingright.repository.RefreshTokenRepository;
import org.example.greetingright.repository.UserRepository;
import org.example.greetingright.security.JwtProperties;
import org.example.greetingright.security.JwtUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RefreshTokenService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken createRefreshToken(String username, String ipAddress){
        Optional<User> user = userRepository.findByUsername(username);
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user.orElse(null)).orElseGet(RefreshToken::new);
        refreshToken.setUser(user.orElse(null));
        refreshToken.setExpiryDate(Instant.now().plusMillis(JwtProperties.REFRESH_EXPIRATION_TIME));
        refreshToken.setIpAddress(ipAddress);
        refreshToken.setToken(jwtUtil.generateRefreshToken(refreshToken));
        return refreshTokenRepository.save(refreshToken);
    }
}