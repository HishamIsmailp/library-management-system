package com.upcode.lms.reservation.service;

import com.upcode.lms.book.entity.Book;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class ReservationDto {
    private Book bookId;
    private Long userId;
    private LocalDate reservationDate;
}
