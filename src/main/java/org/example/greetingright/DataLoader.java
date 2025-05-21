package org.example.greetingright;

import org.example.greetingright.entity.Role;
import org.example.greetingright.entity.User;
import org.example.greetingright.repository.RoleRepository;
import org.example.greetingright.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Run this only if no users exist yet (first app startup with empty database)
        if (userRepository.count() == 0) {
            // Create roles
            Role adminRole = new Role();
            adminRole.setRoleName("ROLE_ADMIN");
            roleRepository.save(adminRole);

            Role userRole = new Role();
            userRole.setRoleName("ROLE_USER");
            roleRepository.save(userRole);

            // Create admin user and assign both ADMIN and USER roles
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin"));
            adminUser.setRoles(Set.of(adminRole, userRole)); // Assign both roles
            userRepository.save(adminUser);

            // Create user account with USER role
            User normalUser = new User();
            normalUser.setUsername("user");
            normalUser.setPassword(passwordEncoder.encode("user"));
            normalUser.setRoles(Set.of(userRole)); // Assign only USER role
            userRepository.save(normalUser);
        }
    }
}