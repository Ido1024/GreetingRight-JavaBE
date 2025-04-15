package org.example.greetingright.repository;

import org.example.greetingright.entity.Wish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishRepository  extends JpaRepository<Wish, Long> {
    List<Wish> findByUserId(Long userId);

}
