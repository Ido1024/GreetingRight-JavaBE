package org.example.greetingright.controller;

import org.example.greetingright.entity.Wish;
import org.example.greetingright.service.WishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @PostMapping("/wish")
    public ResponseEntity<String> getWish() {
        return ResponseEntity.ok("Here’s a wish!");
    }

}
