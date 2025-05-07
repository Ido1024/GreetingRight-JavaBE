package org.example.greetingright.controller;

import org.example.greetingright.dto.UserDTO;
import org.example.greetingright.entity.User;
import org.example.greetingright.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
        System.out.println("GET /api/auth/users called");
        System.out.println("Authorization Header: " + request.getHeader("Authorization")); // Log the Authorization header
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{username}")
    public ResponseEntity<?> updateUser(@PathVariable String username, @RequestBody UserDTO userDTO) {
        User updatedUser = userService.updateUser(
                username,
                userDTO.getUsername(),
                userDTO.getRoles().stream().toList(),
                null // Password update can be handled separately if needed
        );
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok("User deleted successfully");
    }
}