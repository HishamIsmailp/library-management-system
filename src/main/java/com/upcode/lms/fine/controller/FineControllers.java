package com.upcode.lms.fine.controller;

import com.upcode.lms.fine.entity.Fine;
import com.upcode.lms.fine.service.FineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fines")
public class FineControllers {

    private final FineService service;

    public FineControllers(FineService service) {
        this.service = service;
    }
    @GetMapping("/user/{userId}")
    public List<Fine> getByUser(@PathVariable Long userId) {
        return service.getUserFines(userId);
    }

    @PostMapping("/pay/{fineId}")
    public void pay(@PathVariable Long fineId) {
        service.markAsPaid(fineId);
    }
}