package org.example.greetingright.security;

import org.example.greetingright.entity.User;
import org.example.greetingright.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsSerivce implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsSerivce(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("username: + " + username + " not found"));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // Ensure the password is already encoded in the DB
//                .authorities(authorities) // Set authorities from roles
                .build();
    }
}

