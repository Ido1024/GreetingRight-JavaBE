package org.example.greetingright.controller;

import org.example.greetingright.entity.Wish;
import org.example.greetingright.security.JwtUtil;
import org.example.greetingright.service.FlaskWishServiceIMPL;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class WishController {

    private final FlaskWishServiceIMPL flaskWishService;
    private final JwtUtil jwtUtil;

    public WishController(FlaskWishServiceIMPL flaskWishService, JwtUtil jwtUtil) {
        this.flaskWishService = flaskWishService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/wish")
    public ResponseEntity<?> generateWish(@RequestBody Map<String, Object> request, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extract the token from the Authorization header
            String token = authorizationHeader.replace("Bearer ", "");

            // Extract the userId (or username) from the token
            String username = jwtUtil.extractUsername(token);

            System.out.println("Extracted username from token: " + username);

            // Use the username to fetch the userId (if needed)
            Long userId = flaskWishService.getUserIdByUsername(username);

            String userRequest = request.get("text").toString();

            System.out.println("Received request: userId=" + userId + ", text=" + userRequest);

            Wish newWish = flaskWishService.generateWish(userId, userRequest);
            System.out.println("Generated wish: " + newWish.getBirthdayWish());

            return ResponseEntity.ok(Map.of("wish", newWish.getBirthdayWish()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}