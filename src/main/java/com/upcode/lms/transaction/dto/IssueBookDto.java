package com.upcode.lms.transaction.dto;

import com.upcode.lms.auth.entity.User;
import com.upcode.lms.book.entity.BookCopy;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// DTO for issuing a book
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueBookDto {

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private long userId;

    @NotNull(message = "Book copy ID is required")
    @Positive(message = "Book copy ID must be positive")
    private long bookCopyId;
}
