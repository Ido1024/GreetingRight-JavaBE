package org.example.greetingright.service;

import org.example.greetingright.entity.Wish;

import java.util.List;

public interface WishService {
    Wish createWish(Wish wish);
    List<Wish> getWishesByUserId(Long userId);
    List<?> getFavoriteWishesByUserId(Long userId); // Add this method
    Wish getWishById(Long id);
    void deleteWish(Long id);
    List<?> getRecentWishesByUserId(Long userId); // Add this method
    void toggleFavoriteStatus(Long wishId, Long userId);
}