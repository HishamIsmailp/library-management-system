package com.upcode.lms.auth.dto;

public record ChangePasswordDto(
        String password,
        String repeatPassword
) {}
