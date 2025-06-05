package com.upcode.lms.book.repository;

import com.upcode.lms.book.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    List<Genre> findByIsActiveTrueOrderByNameAsc();

    Optional<Genre> findByNameAndIsActiveTrue(String name);

    boolean existsByNameAndIsActiveTrue(String name);

    List<Genre> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);

}
