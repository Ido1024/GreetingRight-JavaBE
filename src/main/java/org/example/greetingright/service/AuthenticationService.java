package org.example.greetingright.service;

import lombok.RequiredArgsConstructor;
import org.example.greetingright.dto.LoginResponseDTO;
import org.example.greetingright.dto.LoginSignupRequestDTO;
import org.example.greetingright.dto.RefreshTokenRequest;
import org.example.greetingright.entity.RefreshToken;
import org.example.greetingright.repository.RefreshTokenRepository;
import org.example.greetingright.security.CustomUserDetailsService;
import org.example.greetingright.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;


    public LoginResponseDTO authenticate(LoginSignupRequestDTO authenticationRequest, String ipAddress) {
        // load the user details from the database using the username by calling the loadUserByUsername() method
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        // check if the password matches the password in the database
        if (!passwordEncoder.matches(authenticationRequest.getPassword(), userDetails.getPassword())) {
            throw new AuthenticationServiceException("Invalid credentials");
        }

        // generate the JWT token
        String jwtToken = jwtUtil.generateToken(userDetails);

        // get the user's roles
        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();

        // return the AuthenticationResponse object
        return generateNewTokens(authenticationRequest.getUsername(), ipAddress);
    }

    public LoginResponseDTO refresh(RefreshTokenRequest refreshToken, String ipAddress) {
        RefreshToken validRefreshToken = refreshTokenRepository.findByToken(refreshToken.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if (!validRefreshToken.getIpAddress().equals(ipAddress)) {
            throw new RuntimeException("IP address mismatch");
        }
        if (validRefreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token ");
        }

        return generateNewTokens(validRefreshToken.getUser().getUsername(), ipAddress);
    }

    public LoginResponseDTO generateNewTokens(String username, String ipAdress) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        String jwtToken = jwtUtil.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(username, ipAdress);
        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();
        return new LoginResponseDTO(jwtToken, refreshToken.getToken(), roles);
    }
}
