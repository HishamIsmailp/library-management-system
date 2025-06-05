package com.upcode.lms.transaction.entity;

import com.upcode.lms.auth.entity.User;
import com.upcode.lms.book.entity.BookCopy;
import com.upcode.lms.common.entity.BaseEntity;
import com.upcode.lms.common.utils.DateUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "book_transactions")
@Getter
@Setter
public class BookTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_copy_id", nullable = false)
    private BookCopy bookCopy;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(name = "renewal_count")
    private Integer renewalCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by")
    private User issuedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "returned_to")
    private User returnedTo;

    @Column(name = "notes")
    private String notes;

    public boolean isOverdue() {
        return returnDate == null && dueDate != null && dueDate.isBefore(LocalDate.now());
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    @PrePersist
    protected void onCreate() {
        super.onCreate(); // Call BaseEntity's lifecycle method
        if (this.issueDate == null) {
            this.issueDate = LocalDate.now();
        }
        if (this.dueDate == null) {
            this.dueDate = DateUtils.calculateDueDate(14); // default loan period
        }
        if (this.renewalCount == null) {
            this.renewalCount = 0;
        }
    }
}
