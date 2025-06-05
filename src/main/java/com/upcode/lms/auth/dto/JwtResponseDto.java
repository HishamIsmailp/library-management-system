package com.upcode.lms.auth.dto;

import com.upcode.lms.auth.entity.Role;

public record JwtResponseDto(
        String type,
        String token,
        String username,
        String email,
        Role role
) {}
