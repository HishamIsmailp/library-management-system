package com.upcode.lms.reservation.repository;

import com.upcode.lms.book.entity.Book;
import com.upcode.lms.reservation.entitiy.BookReservation;
import com.upcode.lms.reservation.entitiy.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<BookReservation, Long> {
    List<BookReservation> findByIdAndStatusOrderByReservationDateAsc(Book bookId, ReservationStatus status);
    List<BookReservation> findByUserId(Long userId);
}
