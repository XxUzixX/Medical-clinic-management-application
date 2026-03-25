package com.example.clinic.config;

import com.example.clinic.entity.Role;
import com.example.clinic.entity.User;
import com.example.clinic.repository.RoleRepository;
import com.example.clinic.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.Collections;

@Component
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    public DataLoader(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Dodanie ról, jeśli nie istnieją
        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_USER");
        createRoleIfNotFound("ROLE_DOCTOR");

        // Dodanie użytkownika admina, jeśli nie istnieje
        if (userRepository.findByEmail("admin") == null) {
            User adminUser = new User();
            adminUser.setName("Admin Admin");
            adminUser.setEmail("admin");
            adminUser.setPassword(passwordEncoder.encode("admin"));
            adminUser.setSpecialization("admin");

            Role adminRole = roleRepository.findByName("ROLE_ADMIN");
            adminUser.setRoles(Collections.singletonList(adminRole)); // Przypisanie roli admina

            entityManager.persist(adminUser);
        }
    }

    private void createRoleIfNotFound(String roleName) {
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            role = new Role(roleName);
            entityManager.persist(role);
        }
    }
}
