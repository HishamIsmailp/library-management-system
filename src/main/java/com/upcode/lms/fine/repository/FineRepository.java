package com.upcode.lms.fine.repository;

import com.upcode.lms.fine.entity.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FineRepository extends JpaRepository<Fine, Long> {
    List<Fine> findByUserId(Long userId);

}