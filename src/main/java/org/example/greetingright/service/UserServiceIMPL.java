package org.example.greetingright.service;

import org.example.greetingright.dto.UserDTO;
import org.example.greetingright.entity.User;
import org.example.greetingright.repository.UserRepository;
import org.example.greetingright.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceIMPL implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public UserServiceIMPL(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User createUser(String username, String rawPassword) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            return null; //todo or throw an exception if you want cleaner error handling
        }
        User user = new User();
        user.setUsername(username); // automatic add the Date onCreate function inside Entity
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setDatasetWishIDs(new HashSet<>());
        return userRepository.save(user);
    }

    @Override
    public User loginUser(String username, String rawPassword) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isEmpty()) {
            return null;
        }
        User user = existingUser.get();
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            return null;
        }
        return user; // valid user
    }
    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDTO(
                        user.getUsername(),
                        user.getCreationDate(),
                        user.getRoles().stream()
                                .map(role -> role.getRoleName()) // Extract role names
                                .collect(Collectors.toSet())
                ))
                .collect(Collectors.toList());
    }
}