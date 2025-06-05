package com.upcode.lms.book.bookCopyRepository;

import com.upcode.lms.book.entity.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCopyRepository extends JpaRepository<BookCopy,Long> {
}
