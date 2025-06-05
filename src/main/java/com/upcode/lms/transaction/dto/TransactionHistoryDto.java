package com.upcode.lms.transaction.dto;

import com.upcode.lms.transaction.entity.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// DTO for transaction history item (response DTO)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryDto {

    private Long id;
    private String bookTitle;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Integer renewalCount;
    private TransactionStatus status;
    private Boolean isOverdue;

}
