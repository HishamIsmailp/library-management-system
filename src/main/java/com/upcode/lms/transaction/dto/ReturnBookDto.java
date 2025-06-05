package com.upcode.lms.transaction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO for returning a book
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnBookDto {

    @NotNull(message = "Transaction ID is required")
    @Positive(message = "Transaction ID must be positive")
    private Long transactionId;

}
