package com.upcode.lms.reservation.service;

import com.upcode.lms.auth.entity.User;
import com.upcode.lms.common.utils.SecurityUtils;
import com.upcode.lms.reservation.entitiy.*;
import com.upcode.lms.reservation.repository.ReservationRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ReservationService {

    private final ReservationRepository repo;

    public ReservationService(ReservationRepository repo) {
        this.repo = repo;
    }

    public BookReservation reserveBook(ReservationDto dto) {
        User userId = com.upcode.lms.common.utils.SecurityUtils.getCurrentUserId();
        BookReservation reservation = new BookReservation();
        reservation.setBookId(dto.getBookId());
        reservation.setUserId(userId);
        reservation.setReservationDate(LocalDate.now());
        reservation.setExpiryDate(LocalDate.now().plusDays(7));
        reservation.setStatus(ReservationStatus.PENDING);
        return repo.save(reservation);
    }

    public List<BookReservation> getUserReservations(Long userId) {
        SecurityUtils.canAccessUserResource(userId);
        return repo.findByUserId(userId);
    }
}
