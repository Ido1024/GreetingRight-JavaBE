package org.example.greetingright.repository;

import org.example.greetingright.entity.DatasetWish;
import org.example.greetingright.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DatasetWishRepository extends JpaRepository<DatasetWish, Long> {
    List<DatasetWish> findByUser(User user);
}