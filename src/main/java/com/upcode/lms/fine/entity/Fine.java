package com.upcode.lms.fine.entity;

import com.upcode.lms.common.entity.BaseEntity;
import com.upcode.lms.transaction.BookTransaction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Transaction;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Fine extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "fine_id")
    private Fine fine;

    private Long userId;


    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private BookTransaction transactionId; // could reference a return or checkout record

    private Double fineAmount;

    private String fineReason;

    private LocalDate fineDate;

    private LocalDate paymentDate;

    private Double paymentAmount;

    private String paymentMethod; // e.g., "CASH", "CARD", "ONLINE"

    @Enumerated(EnumType.STRING)
    private FineStatus status; // PENDING, PAID, OVERDUE, WAIVED

    private Long waivedBy; // FK to staff user who waived

    private String waivedReason;


}
