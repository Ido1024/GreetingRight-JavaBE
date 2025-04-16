package org.example.greetingright.service;

import org.example.greetingright.dto.UserDTO;
import org.example.greetingright.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    User createUser(String username, String password);
    User loginUser(String username, String password);
    List<UserDTO> getAllUsers();
}
