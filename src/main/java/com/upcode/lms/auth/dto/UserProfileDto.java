package com.upcode.lms.auth.dto;

import com.upcode.lms.auth.entity.Role;

import java.time.LocalDateTime;

public record UserProfileDto(
        Long id,
        String username,
        String email,
        String fullName,
        String firstName,
        String lastName,
        Role role,
        String phoneNumber,
        Boolean isEmailVerified,
        LocalDateTime lastLogin,
        LocalDateTime createdAt
) {
}
