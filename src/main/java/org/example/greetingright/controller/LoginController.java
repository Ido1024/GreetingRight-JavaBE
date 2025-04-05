package org.example.greetingright.controller;

import org.example.greetingright.dto.LoginSignupRequestDTO;
import org.example.greetingright.entity.User;
import org.example.greetingright.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.example.greetingright.repository.UserRepository;

import java.util.Optional;

@RestController
public class LoginController {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserService userService;

    public LoginController(PasswordEncoder passwordEncoder, UserRepository userRepository, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    //todo return jwt
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginSignupRequestDTO dto) {
        User user = userService.loginUser(dto.getUsername(), dto.getPassword());
        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }

        // Optionally generate and return a JWT here
        return ResponseEntity.ok("Login successful");
    }
}
