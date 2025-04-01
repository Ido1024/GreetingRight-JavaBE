package org.example.greetingright.controller;

import org.example.greetingright.dto.LoginSignupRequestDTO;
import org.example.greetingright.entity.User;
import org.example.greetingright.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class SignupController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody LoginSignupRequestDTO loginSignupRequestDTO) {
        Optional<User> existingUser = userRepository.findByUsername(loginSignupRequestDTO.getUsername());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        User user = new User();
        user.setUsername(loginSignupRequestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(loginSignupRequestDTO.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok().body("User successfully created");
    }
}
