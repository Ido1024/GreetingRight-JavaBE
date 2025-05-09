package org.example.greetingright.controller;

import org.example.greetingright.entity.Wish;
import org.example.greetingright.security.JwtUtil;
import org.example.greetingright.service.FlaskWishServiceIMPL;
import org.example.greetingright.service.WishServiceIMPL;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class WishController {

    private final FlaskWishServiceIMPL flaskWishService;
    private final JwtUtil jwtUtil;
    private final WishServiceIMPL wishService;

    public WishController(FlaskWishServiceIMPL flaskWishService, JwtUtil jwtUtil, WishServiceIMPL wishService) {
        this.flaskWishService = flaskWishService;
        this.jwtUtil = jwtUtil;
        this.wishService = wishService;
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

    //todo dont think it works, check if it does. maybe need to add a function for the favorite button, casue i dont think i have one
    @GetMapping("/wishes/recent")
    public ResponseEntity<?> getRecentWishes(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extract the token from the Authorization header
            String token = authorizationHeader.replace("Bearer ", "");

            // Extract the username from the token
            String username = jwtUtil.extractUsername(token);

            // Fetch the userId using the username
            System.out.println("Extracted username from token: " + username);

            // Use the username to fetch the userId (if needed)
            Long userId = flaskWishService.getUserIdByUsername(username);

            // Fetch the recent 3 wishes for the user
            List<?> recentWishes = wishService.getRecentWishesByUserId(userId);

            return ResponseEntity.ok(recentWishes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/wishes/favorites")
    public ResponseEntity<?> getFavoriteWishes(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extract the token from the Authorization header
            String token = authorizationHeader.replace("Bearer ", "");

            // Extract the username from the token
            String username = jwtUtil.extractUsername(token);

            // Fetch the userId using the username
            System.out.println("Extracted username from token: " + username);

            // Use the username to fetch the userId (if needed)
            Long userId = flaskWishService.getUserIdByUsername(username);

            // Fetch the favorite wishes for the user
            List<?> favoriteWishes = wishService.getFavoriteWishesByUserId(userId);

            return ResponseEntity.ok(favoriteWishes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/wishes/{wishId}/favorite")
    public ResponseEntity<?> toggleFavoriteStatus(
            @PathVariable Long wishId,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extract the token from the Authorization header
            String token = authorizationHeader.replace("Bearer ", "");

            // Extract the username from the token
            String username = jwtUtil.extractUsername(token);

            // Fetch the userId using the username
            Long userId = flaskWishService.getUserIdByUsername(username);

            // Toggle the favorite status in the database
            wishService.toggleFavoriteStatus(wishId, userId);

            return ResponseEntity.ok("Favorite status toggled successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error toggling favorite status: " + e.getMessage());
        }
    }
}