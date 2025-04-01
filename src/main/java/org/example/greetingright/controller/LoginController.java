package org.example.greetingright.controller;

import org.example.greetingright.dto.LoginSignupRequestDTO;
import org.example.greetingright.entity.User;
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

    public LoginController(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    //todo return jwt
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginSignupRequestDTO loginRequestDTO) {
        String username = loginRequestDTO.getUsername();
        String password = loginRequestDTO.getPassword();
        if (userRepository.findByUsername(username) != null) {
            Optional<User> temp = userRepository.findByUsername(username);
            User user = temp.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.ok().body("Login successful");
            }
        }
        return ResponseEntity.badRequest().body("Invalid username or password");
    }
}
