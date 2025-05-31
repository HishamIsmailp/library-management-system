package com.upcode.lms.config;

import com.upcode.lms.auth.entity.Role;
import com.upcode.lms.auth.entity.User;
import com.upcode.lms.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            createDefaultUsers();
        };
    }

    private void createDefaultUsers() {
        log.info("Initializing default users...");

        // Create Admin user
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@library.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("System")
                    .lastName("Administrator")
                    .phoneNumber("+1234567890")
                    .role(Role.ADMIN)
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .emailVerified(true)
                    .build();
            
            userRepository.save(admin);
            log.info("Created admin user: admin/admin123");
        }

        // Create Librarian user
        if (!userRepository.existsByUsername("librarian")) {
            User librarian = User.builder()
                    .username("librarian")
                    .email("librarian@library.com")
                    .password(passwordEncoder.encode("librarian123"))
                    .firstName("John")
                    .lastName("Librarian")
                    .phoneNumber("+1234567891")
                    .role(Role.LIBRARIAN)
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .emailVerified(true)
                    .build();
            
            userRepository.save(librarian);
            log.info("Created librarian user: librarian/librarian123");
        }

        // Create Student user
        if (!userRepository.existsByUsername("student")) {
            User student = User.builder()
                    .username("student")
                    .email("student@university.edu")
                    .password(passwordEncoder.encode("student123"))
                    .firstName("Jane")
                    .lastName("Student")
                    .phoneNumber("+1234567892")
                    .dateOfBirth(LocalDate.of(2000, 5, 15))
                    .address("123 University Street, City, State 12345")
                    .studentId("STU001")
                    .role(Role.STUDENT)
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .emailVerified(true)
                    .build();
            
            userRepository.save(student);
            log.info("Created student user: student/student123");
        }

        // Create additional test students
        for (int i = 2; i <= 5; i++) {
            String username = "student" + i;
            if (!userRepository.existsByUsername(username)) {
                User student = User.builder()
                        .username(username)
                        .email("student" + i + "@university.edu")
                        .password(passwordEncoder.encode("password123"))
                        .firstName("Student")
                        .lastName("User" + i)
                        .phoneNumber("+123456789" + i)
                        .dateOfBirth(LocalDate.of(1999 + i, 3, 10))
                        .address(i + " Student Avenue, Campus, State 12345")
                        .studentId("STU00" + i)
                        .role(Role.STUDENT)
                        .enabled(true)
                        .accountNonExpired(true)
                        .accountNonLocked(true)
                        .credentialsNonExpired(true)
                        .emailVerified(true)
                        .build();
                
                userRepository.save(student);
                log.info("Created test student: {}/password123", username);
            }
        }

        log.info("Data initialization completed successfully!");
    }
}