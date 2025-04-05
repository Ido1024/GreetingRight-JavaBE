package org.example.greetingright.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.greetingright.dto.LoginResponseDTO;
import org.example.greetingright.dto.LoginSignupRequestDTO;
import org.example.greetingright.entity.User;
import org.example.greetingright.service.AuthenticationService;
import org.example.greetingright.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    public LoginController(UserService userService, AuthenticationService authenticationService) {

        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    //todo return jwt
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginSignupRequestDTO loginSignupRequestDTO, HttpServletRequest request) {
        User user = userService.loginUser(loginSignupRequestDTO.getUsername(), loginSignupRequestDTO.getPassword());
        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
        LoginResponseDTO authResponse = authenticationService.authenticate(loginSignupRequestDTO,request.getRemoteAddr());
        System.out.println("jwt token: " + authResponse.getAccessToken());
        System.out.println("Refresh token: " + authResponse.getRefreshToken());
        System.out.println("roles: " + authResponse.getRoles());
        System.out.println("username: " + loginSignupRequestDTO.getUsername());
        // Optionally generate and return a JWT here
        return ResponseEntity.ok("Login successful");
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody String refreshToken, HttpServletRequest request) {
        try {
            System.out.println("refresh token: " + refreshToken);
            LoginResponseDTO authResponse = authenticationService.refresh(refreshToken, request.getRemoteAddr());
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
