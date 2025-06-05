package com.upcode.lms.transaction.controller;

import com.upcode.lms.common.dto.ApiResponse;
import com.upcode.lms.common.dto.PageRequest;
import com.upcode.lms.common.dto.PageResponse;
import com.upcode.lms.common.utils.SecurityUtils;
import com.upcode.lms.transaction.dto.IssueBookDto;
import com.upcode.lms.transaction.dto.ReturnBookDto;

import com.upcode.lms.transaction.dto.TransactionHistoryDto;
import com.upcode.lms.transaction.entity.BookTransaction;
import com.upcode.lms.transaction.service.TransactionService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/transactions")
@Validated
@RequiredArgsConstructor
@Tag(name = "Transaction Management",description = "APIs for issuing, returning , renewing book and transaction history")
public class TransactionController {

    private final TransactionService transactionService;


    @PostMapping("/issue")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<BookTransaction>> issueBook(@Valid @RequestBody IssueBookDto dto) {
        log.info("POST /api/transactions/issue - Issuing book to userId: {}", dto.getUserId());
        BookTransaction transaction = transactionService.issueBook(dto, SecurityUtils.getCurrentUserOrThrow());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(transaction,"Book issued successfully"));
    }


    @PostMapping("/return")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<BookTransaction>> returnBook(@Valid @RequestBody ReturnBookDto dto) {
        log.info("POST /api/transaction/return - Returning transactionId: {} ",dto.getTransactionId());
        BookTransaction transaction = transactionService.returnBook(dto, SecurityUtils.getCurrentUserOrThrow());
        return ResponseEntity.ok(ApiResponse.success(transaction,"Book returned successfully"));
    }


    @PostMapping("/renew/{transactionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<BookTransaction>> renewBook(@Valid @PathVariable long transactionId) {
        log.info("POST /api/transaction/renew - Returning transactionId: {}",transactionId);
        BookTransaction transaction = transactionService.renewBook(transactionId);
        return ResponseEntity.ok(ApiResponse.success(transaction,"Book renewed successfully"));
    }


    @GetMapping("/history")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<PageResponse<TransactionHistoryDto>>> getUserHistory(
            @Valid PageRequest request,
            @RequestParam Long userId) {
        log.info("GET /api/transactions/history - userId: {}", userId);
        PageResponse<TransactionHistoryDto> history = transactionService.getTransactionHistory(userId, request);
        return ResponseEntity.ok(ApiResponse.success(history, "Transaction history retrieved successfully"));
    }
}
