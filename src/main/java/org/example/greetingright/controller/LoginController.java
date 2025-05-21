package org.example.greetingright.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.greetingright.dto.LoginResponseDTO;
import org.example.greetingright.dto.LoginSignupRequestDTO;
import org.example.greetingright.dto.RefreshTokenRequest;
import org.example.greetingright.entity.User;
import org.example.greetingright.service.AuthenticationService;
import org.example.greetingright.service.RefreshTokenService;
import org.example.greetingright.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final HttpServletRequest request;
    private final RefreshTokenService refreshTokenService;

    public LoginController(UserService userService, AuthenticationService authenticationService, HttpServletRequest request, RefreshTokenService refreshTokenService) {

        this.userService = userService;
        this.authenticationService = authenticationService;
        this.request = request;
        this.refreshTokenService = refreshTokenService;
    }

    @GetMapping("/home")
    public String home() {
        return "welcome to the home page";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginSignupRequestDTO loginSignupRequestDTO, HttpServletRequest request) {
        User user = userService.loginUser(loginSignupRequestDTO.getUsername(), loginSignupRequestDTO.getPassword());
        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
        String ipAddress = request.getRemoteAddr(); // Get the IP address from the request
        LoginResponseDTO authResponse = authenticationService.authenticate(loginSignupRequestDTO, ipAddress);
        System.out.println("jwt token: " + authResponse.getAccessToken());
        System.out.println("Refresh token: " + authResponse.getRefreshToken());
        System.out.println("roles: " + authResponse.getRoles());
        System.out.println("username: " + loginSignupRequestDTO.getUsername());
        return ResponseEntity.ok(authResponse);
    }
    // Invalidate the refresh token so the user can't get
    // new access tokens after logout (access tokens are stateless and expire on their own).
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest requestT) {
        String refreshToken = requestT.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Refresh token is required .");
        }
        String ipAddress = request.getRemoteAddr(); // Get the IP address from the request
        LoginResponseDTO authResponse = authenticationService.refresh(requestT, ipAddress);
        return ResponseEntity.ok(authResponse);
    }

    // Invalidate the refresh token so the user can't get new access tokens after logout
    // (access tokens are stateless and expire on their own).
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest requestT) {
        String refreshToken = requestT.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Refresh token is required.");
        }
        refreshTokenService.invalidateRefreshToken(refreshToken);
        return ResponseEntity.ok("Logged out successfully.");
    }
}
