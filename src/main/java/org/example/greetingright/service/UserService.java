package org.example.greetingright.service;

import org.example.greetingright.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);

}
