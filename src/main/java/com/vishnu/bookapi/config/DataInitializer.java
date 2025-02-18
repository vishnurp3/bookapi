package com.vishnu.bookapi.config;

import com.vishnu.bookapi.entity.Role;
import com.vishnu.bookapi.entity.User;
import com.vishnu.bookapi.repository.RoleRepository;
import com.vishnu.bookapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;
    @Value("${USER_PASSWORD}")
    private String userPassword;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing default roles and users...");
        Role adminRole = createRoleIfNotFound("ROLE_ADMIN");
        Role userRole = createRoleIfNotFound("ROLE_USER");
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode(adminPassword))
                    .roles(Set.of(adminRole, userRole))
                    .build();
            userRepository.save(adminUser);
            log.info("Admin user created with username: 'admin'");
        } else {
            log.info("Admin user already exists.");
        }
        if (userRepository.findByUsername("user").isEmpty()) {
            User normalUser = User.builder()
                    .username("user")
                    .password(passwordEncoder.encode(userPassword))
                    .roles(Set.of(userRole))
                    .build();
            userRepository.save(normalUser);
            log.info("Normal user created with username: 'user'");
        } else {
            log.info("Normal user already exists.");
        }
    }

    private Role createRoleIfNotFound(String roleName) {
        Optional<Role> roleOptional = roleRepository.findByName(roleName);
        if (roleOptional.isPresent()) {
            return roleOptional.get();
        }
        Role role = Role.builder()
                .name(roleName)
                .build();
        roleRepository.save(role);
        log.info("Created role: {}", roleName);
        return role;
    }
}
