package org.example.greetingright.service;

import org.example.greetingright.dto.WishDTO;
import org.example.greetingright.entity.Wish;
import org.example.greetingright.repository.WishRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishServiceIMPL implements WishService {

    private final WishRepository wishRepository;

    public WishServiceIMPL(WishRepository wishRepository) {
        this.wishRepository = wishRepository;
    }

    @Override
    public Wish createWish(Wish wish) {
        return wishRepository.save(wish);
    }

    @Override
    public List<Wish> getWishesByUserId(Long userId) {
        return wishRepository.findByUserId(userId);
    }

    @Override
    public List<WishDTO> getRecentWishesByUserId(Long userId) {
        List<Wish> wishes = wishRepository.findByUserId(userId);

        // Sort by creation date (descending) and limit to 3, then map to WishDTO
        return wishes.stream()
                .sorted((w1, w2) -> w2.getCreationDate().compareTo(w1.getCreationDate()))
                .limit(3)
                .map(wish -> new WishDTO(
                        wish.getId(),
                        wish.getBirthdayWish(),
                        wish.getCreationDate(),
                        wish.isFavorite()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<WishDTO> getFavoriteWishesByUserId(Long userId) {
        List<Wish> wishes = wishRepository.findByUserId(userId);

        // Filter only favorite wishes, then map to WishDTO
        return wishes.stream()
                .filter(Wish::isFavorite) // Only include favorite wishes
                .map(wish -> new WishDTO(
                        wish.getId(),
                        wish.getBirthdayWish(),
                        wish.getCreationDate(),
                        wish.isFavorite()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Wish getWishById(Long id) {
        return wishRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteWish(Long id) {
        wishRepository.deleteById(id);
    }

    @Override
    public void toggleFavoriteStatus(Long wishId, Long userId) {
        // Fetch the wish by ID and user ID to ensure the wish belongs to the user
        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new RuntimeException("Wish not found"));

        if (!wish.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to update this wish.");
        }

        // Toggle the favorite status
        wish.setFavorite(!wish.isFavorite());

        // Save the updated wish
        wishRepository.save(wish);
    }
}