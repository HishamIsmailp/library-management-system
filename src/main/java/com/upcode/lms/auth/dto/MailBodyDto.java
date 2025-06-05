package com.upcode.lms.auth.dto;

import lombok.Builder;

@Builder
public record MailBodyDto(
        String to,
        String subject,
        String text
) {}
