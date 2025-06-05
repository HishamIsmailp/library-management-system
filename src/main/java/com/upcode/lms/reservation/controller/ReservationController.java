package com.upcode.lms.reservation.controller;

import com.upcode.lms.reservation.service.ReservationDto;
import com.upcode.lms.reservation.service.ReservationService;
import org.springframework.web.bind.annotation.*;
import com.upcode.lms.reservation.entitiy.BookReservation;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @PostMapping
    public BookReservation create(@RequestBody ReservationDto dto) {
        return service.reserveBook(dto);
    }

    @GetMapping("/user/{userId}")
    public List<BookReservation> getByUser(@PathVariable Long userId) {
        return service.getUserReservations(userId);
    }
}