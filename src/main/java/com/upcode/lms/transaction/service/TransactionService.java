package com.upcode.lms.transaction.service;

import com.upcode.lms.auth.entity.User;
import com.upcode.lms.auth.repository.UserRepository;
import com.upcode.lms.book.bookCopyRepository.BookCopyRepository;
import com.upcode.lms.book.entity.BookCopy;
import com.upcode.lms.common.dto.PageRequest;
import com.upcode.lms.common.dto.PageResponse;
import com.upcode.lms.common.exception.LibraryBusinessException;
import com.upcode.lms.common.exception.ResourceNotFoundException;
import com.upcode.lms.common.utils.SecurityUtils;
import com.upcode.lms.transaction.dto.IssueBookDto;
import com.upcode.lms.transaction.dto.ReturnBookDto;
import com.upcode.lms.transaction.dto.TransactionHistoryDto;
import com.upcode.lms.transaction.entity.BookTransaction;
import com.upcode.lms.transaction.entity.TransactionStatus;
import com.upcode.lms.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final DueDateService dueDateService;
    private final BookCopyRepository bookCopyRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookTransaction issueBook(IssueBookDto dto, User currentUser) {
        log.debug("Issuing book to user ID: {}, bookCopy ID: {}", dto.getUserId(), dto.getBookCopyId());
        SecurityUtils.ensureStaff();


        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + dto.getUserId()));

        BookCopy bookCopy = bookCopyRepository.findById(dto.getBookCopyId())
                .orElseThrow(() -> new IllegalArgumentException("Book copy not found with ID: " + dto.getBookCopyId()));


        BookTransaction transaction = new BookTransaction();
        transaction.setUser(user);
        transaction.setBookCopy(bookCopy);
        transaction.setIssueDate(LocalDate.now());
        transaction.setDueDate(dueDateService.calculateDueDate(14));
        transaction.setStatus(TransactionStatus.ISSUED);
        transaction.setIssuedBy(currentUser);

        BookTransaction savedTransaction = transactionRepository.save(transaction);
        log.info("Issued book transaction ID: {} by staff: {}", savedTransaction.getId(), currentUser.getUsername());

        return savedTransaction;
    }


    @Transactional
    public BookTransaction returnBook(ReturnBookDto dto, User currentUser) {
        log.debug("Returning book for transaction ID: {}", dto.getTransactionId());
        SecurityUtils.ensureStaff();

        BookTransaction transaction = transactionRepository.findById(dto.getTransactionId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", dto.getTransactionId()));

        if (transaction.getStatus() == TransactionStatus.RETURNED) {
            throw LibraryBusinessException.bookAlreadyIssued(
                    transaction.getBookCopy().getBook().getTitle(), currentUser.getUsername());
        }

        transaction.setReturnDate(LocalDate.now());
        transaction.setReturnedTo(currentUser);
        transaction.setStatus(TransactionStatus.RETURNED);

        BookTransaction savedTransaction = transactionRepository.save(transaction);
        log.info("Returned book transaction ID: {} by staff: {}", savedTransaction.getId(), currentUser.getUsername());

        return savedTransaction;
    }

    @Transactional
    public BookTransaction renewBook(Long transactionId) {
        log.debug("Renewing book for transaction ID: {}", transactionId);
        SecurityUtils.ensureStaff();

        BookTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId));

        transaction.setDueDate(dueDateService.calculateDueDate(14));
        transaction.setRenewalCount(transaction.getRenewalCount() + 1);
        transaction.setStatus(TransactionStatus.RENEWED);

        BookTransaction updatedTransaction = transactionRepository.save(transaction);
        log.info("Renewed book transaction ID: {}", updatedTransaction.getId());

        return updatedTransaction;
    }

    public PageResponse<TransactionHistoryDto> getTransactionHistory(Long userId, PageRequest request) {
        log.debug("Fetching transaction history for user ID: {}", userId);
        SecurityUtils.ensureCanAccessUserResource(userId);

        Page<BookTransaction> page = transactionRepository.findByUserId(
                userId, request.toPageRequest("issueDate"));

        return PageResponse.of(page.map(this::mapToHistoryDto));
    }

    private TransactionHistoryDto mapToHistoryDto(BookTransaction transaction) {
        return TransactionHistoryDto.builder()
                .id(transaction.getId())
                .bookTitle(transaction.getBookCopy().getBook().getTitle())
                .issueDate(transaction.getIssueDate())
                .dueDate(transaction.getDueDate())
                .returnDate(transaction.getReturnDate())
                .status(transaction.getStatus())
                .build();
    }
}
