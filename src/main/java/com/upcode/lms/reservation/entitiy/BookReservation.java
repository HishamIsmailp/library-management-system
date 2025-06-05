
package com.upcode.lms.reservation.entitiy;

import com.upcode.lms.auth.entity.User;
import com.upcode.lms.book.entity.Book;
import com.upcode.lms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@Entity
public class BookReservation extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book bookId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    private LocalDate reservationDate;
    private LocalDate expiryDate;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
    @Column(name = "notified", nullable = false)
    private Boolean notified = false;

}