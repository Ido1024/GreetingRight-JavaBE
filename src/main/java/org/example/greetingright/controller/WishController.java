package org.example.greetingright.controller;

import org.example.greetingright.entity.Wish;
import org.example.greetingright.service.FlaskWishServiceIMPL;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class WishController {

    private final FlaskWishServiceIMPL flaskWishService;

    public WishController(FlaskWishServiceIMPL flaskWishService) {
        this.flaskWishService = flaskWishService;
    }

    @PostMapping("/wish")
    public ResponseEntity<?> generateWish(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        String userRequest = request.get("text").toString();

        Wish newWish = flaskWishService.generateWish(userId, userRequest);
        return ResponseEntity.ok(newWish);
    }
}