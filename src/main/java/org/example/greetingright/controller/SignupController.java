package org.example.greetingright.controller;

import org.example.greetingright.dto.LoginSignupRequestDTO;
import org.example.greetingright.entity.User;
import org.example.greetingright.repository.UserRepository;
import org.example.greetingright.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class SignupController {
    private final UserService userService;

    public SignupController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody LoginSignupRequestDTO dto) {
        try {
            User createdUser = userService.createUser(dto.getUsername(), dto.getPassword());
            if (createdUser == null) {
                return ResponseEntity.badRequest().body("Username already exists");
            }
            return ResponseEntity.ok("User successfully created");
        } catch (Exception e) {
            e.printStackTrace(); // Or use a logging framework
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while signing up");
        }
    }

}
