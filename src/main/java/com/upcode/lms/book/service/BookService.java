package com.upcode.lms.book.service;

import com.upcode.lms.book.dto.BookCreationDto;
import com.upcode.lms.book.dto.BookDto;
import com.upcode.lms.book.dto.BookSearchDto;
import com.upcode.lms.book.repository.AuthorRepository;
import com.upcode.lms.book.repository.GenreRepository;
import com.upcode.lms.common.dto.PageRequest;
import com.upcode.lms.common.dto.PageResponse;
import com.upcode.lms.book.entity.Book;
import com.upcode.lms.book.repository.BookRepository;

import com.upcode.lms.common.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final FileUploadService fileUploadService;

    public Page<BookDto> getAllBooks(Pageable pageable) {
        Page<Book> page = bookRepository.findByIsActiveTrue(pageable);
        return page.map(this::toDto);
    }
    public Page<Book> advancedSearch(BookSearchDto searchDto, Pageable pageable) {
        return bookRepository.findByFilters(
            searchDto.getTitle(),
            searchDto.getAuthorName(),
            searchDto.getGenreName(),
            searchDto.getLanguage(),
            pageable
        );
    }

    public Page<BookDto> searchBooks(BookSearchDto searchDto, Pageable pageable) {
        Page<Book> page = bookRepository.findByFilters(
            searchDto.getTitle(),
            searchDto.getAuthorName(),
            searchDto.getGenreName(),
            searchDto.getLanguage(),
            pageable
        );
        return page.map(this::toDto);
    }

    public BookDto getById(Long id) {
        Book book = bookRepository.findById(id)
            .filter(Book::getIsActive)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return toDto(book);
    }

    public BookDto create(BookCreationDto dto) {
        Book book = new Book();
        mapDtoToEntity(dto, book);
        book.setIsActive(true);
        return toDto(bookRepository.save(book));
    }

    public BookDto update(Long id, BookCreationDto dto) {
        Book book = bookRepository.findById(id)
            .filter(Book::getIsActive)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        mapDtoToEntity(dto, book);
        return toDto(bookRepository.save(book));
    }

    public void softDelete(Long id) {
        Book book = bookRepository.findById(id)
            .filter(Book::getIsActive)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        book.setIsActive(false);
        bookRepository.save(book);
    }

    private void mapDtoToEntity(BookCreationDto dto, Book book) {
        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setDescription(dto.getDescription());
        book.setPublicationDate(dto.getPublicationDate());
        book.setPages(dto.getPages());
        book.setLanguage(dto.getLanguage());
        book.setEdition(dto.getEdition());
        book.setCoverImageUrl(dto.getCoverImageUrl());
        book.setTotalCopies(dto.getTotalCopies());
        book.setAvailableCopies(dto.getTotalCopies());

        if (dto.getCoverImageFile() != null && !dto.getCoverImageFile().isEmpty()) {
            try {
                String fileName = fileUploadService.saveFile(dto.getCoverImageFile());
                book.setCoverImageUrl("/uploads/" + fileName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload cover image", e);
            }
        }

        book.setAuthor(authorRepository.findById(dto.getAuthorId())
            .orElseThrow(() -> new ResourceNotFoundException("Author not found")));
        book.setGenre(genreRepository.findById(dto.getGenreId())
            .orElseThrow(() -> new ResourceNotFoundException("Genre not found")));
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

        Optional.ofNullable(book.getAuthor()).ifPresent(a -> dto.setAuthorName(a.getName()));
        Optional.ofNullable(book.getPublisher()).ifPresent(p -> dto.setPublisherName(p.getName()));
        Optional.ofNullable(book.getGenre()).ifPresent(g -> dto.setGenreName(g.getName()));

        return dto;
    }
}
