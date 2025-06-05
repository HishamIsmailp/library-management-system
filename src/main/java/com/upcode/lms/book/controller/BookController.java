package com.upcode.lms.book.controller;

import com.upcode.lms.book.dto.BookCreationDto;
import com.upcode.lms.book.dto.BookDto;
import com.upcode.lms.book.dto.BookSearchDto;
import com.upcode.lms.book.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Books", description = "Book management APIs")
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;


    @GetMapping
    public ResponseEntity<Page<BookDto>> getAllBooks(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        var pageable = PageRequest.of(page, size);
        var books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "Get a book by ID")
    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        var dto = bookService.getById(id);
        return ResponseEntity.ok(dto);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PostMapping
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookCreationDto dto) {
        var created = bookService.create(dto);
        return ResponseEntity.status(201).body(created);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(
        @PathVariable Long id,
        @Valid @RequestBody BookCreationDto dto) {
        var updated = bookService.update(id, dto);
        return ResponseEntity.ok(updated);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
