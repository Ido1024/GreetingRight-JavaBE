package org.example.greetingright.service;

import org.example.greetingright.entity.Wish;
import org.example.greetingright.repository.WishRepository;
import org.example.greetingright.service.WishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Wish getWishById(Long id) {
        return wishRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteWish(Long id) {
        wishRepository.deleteById(id);
    }
}
