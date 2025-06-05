package com.upcode.lms.transaction.repository;

import com.upcode.lms.transaction.entity.BookTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<BookTransaction,Long> {
    Page<BookTransaction> findByUserId(Long userId, Pageable pageable);
}
