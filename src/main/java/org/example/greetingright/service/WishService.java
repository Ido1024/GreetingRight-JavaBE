package org.example.greetingright.service;

import org.example.greetingright.entity.Wish;

import java.util.List;
import java.util.Optional;

public interface WishService {
    Wish createWish(Wish wish);
    List<Wish> getWishesByUserId(Long userId);
    Wish getWishById(Long id);
    void deleteWish(Long id);
}
