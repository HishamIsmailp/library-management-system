package com.upcode.lms.book.repository;

import com.upcode.lms.book.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    List<Author> findByFirstNameContainingIgnoreCase(String firstName);

    List<Author> findByLastNameContainingIgnoreCase(String lastName);

    List<Author> findByIsActiveTrue();

    List<Author> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);

}
