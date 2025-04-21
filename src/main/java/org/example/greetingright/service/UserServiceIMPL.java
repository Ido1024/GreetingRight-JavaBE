package org.example.greetingright.service;

import org.example.greetingright.dto.UserDTO;
import org.example.greetingright.entity.Role;
import org.example.greetingright.entity.User;
import org.example.greetingright.repository.RoleRepository;
import org.example.greetingright.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceIMPL implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceIMPL.class); // Add logger
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserServiceIMPL(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User createUser(String username, String rawPassword) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            return null; // Or throw an exception for cleaner error handling
        }

        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        User user = new User();
        user.setRoles(new HashSet<>(Set.of(userRole)));
        user.setUsername(username); // Automatically sets the creation date via @PrePersist in the User entity
        user.setPassword(passwordEncoder.encode(rawPassword));

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

        return user; // Valid user
    }

    @Override
    public List<UserDTO> getAllUsers() {
        logger.info("Fetching all users from the database");
        return userRepository.findAll().stream()
                .map(user -> new UserDTO(
                        user.getUsername(),
                        user.getCreationDate(),
                        user.getRoles().stream()
                                .map(Role::getRoleName) // Extract role names
                                .collect(Collectors.toSet())
                ))
                .collect(Collectors.toList());
    }
}