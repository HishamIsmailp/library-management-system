package com.upcode.lms.fine.service;

import com.upcode.lms.common.utils.SecurityUtils;
import com.upcode.lms.fine.entity.Fine;
import com.upcode.lms.fine.entity.FineStatus;
import com.upcode.lms.fine.repository.FineRepository;

import java.util.List;

public class FineService {
    private final FineRepository repo;

    public FineService(FineRepository repo) {
        this.repo = repo;
    }

    public List<Fine> getUserFines(Long userId) {
        SecurityUtils.canAccessUserResource(userId);
        return repo.findByUserId(userId);
    }

    public void markAsPaid(Long fineId) {
        SecurityUtils.ensureStaff();
        Fine fine = repo.findById(fineId).orElseThrow();
        fine.setFine(fine);
        fine.setStatus(FineStatus.PAID);
        repo.save(fine);
    }
}

