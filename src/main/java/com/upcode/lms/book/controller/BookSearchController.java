package com.upcode.lms.book.controller;

import com.upcode.lms.book.dto.BookDto;
import com.upcode.lms.book.dto.BookSearchDto;
import com.upcode.lms.book.entity.Book;
import com.upcode.lms.book.service.BookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Book Search", description = "Advanced search APIs for Books")
@RestController
@RequestMapping("/books/search")
@RequiredArgsConstructor
public class BookSearchController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<Page<BookDto>> searchBooks(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String authorName,
        @RequestParam(required = false) String genreName,
        @RequestParam(required = false) String language,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        BookSearchDto searchDto = new BookSearchDto();
        searchDto.setTitle(title);
        searchDto.setAuthorName(authorName);
        searchDto.setGenreName(genreName);
        searchDto.setLanguage(language);

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookService.advancedSearch(searchDto, pageable);


        Page<BookDto> bookDtoPage = books.map(this::toDto);
        return ResponseEntity.ok(bookDtoPage);
    }

    private BookDto toDto(Book book) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setDescription(book.getDescription());
        dto.setPublicationDate(book.getPublicationDate());
        dto.setPages(book.getPages());
        dto.setLanguage(book.getLanguage());
        dto.setEdition(book.getEdition());
        dto.setCoverImageUrl(book.getCoverImageUrl());
        dto.setTotalCopies(book.getTotalCopies());
        dto.setAvailableCopies(book.getAvailableCopies());

        if (book.getAuthor() != null) {
            dto.setAuthorName(book.getAuthor().getName());
        }
        if (book.getPublisher() != null) {
            dto.setPublisherName(book.getPublisher().getName());
        }
        if (book.getGenre() != null) {
            dto.setGenreName(book.getGenre().getName());
        }
        return dto;
    }
}
