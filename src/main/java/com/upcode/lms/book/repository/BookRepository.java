package com.upcode.lms.book.repository;

import com.upcode.lms.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {


    List<Book> findByIsActiveTrue();

    Optional<Book> findByTitleAndIsActiveTrue(String title);

    boolean existsByTitleAndIsActiveTrue(String title);

    boolean existsByTitleAndIdNotAndIsActiveTrue(String title, Long id);


    Page<Book> findByIsActiveTrue(Pageable pageable);


    @Query("SELECT b FROM Book b WHERE " +
        "(LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
        "LOWER(b.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
        "b.isActive = true")
    Page<Book> searchBooks(@Param("search") String search, Pageable pageable);


    Page<Book> findByGenreIdAndIsActiveTrue(Long genreId, Pageable pageable);


    Page<Book> findByAuthorIdAndIsActiveTrue(Long authorId, Pageable pageable);


    long countByIsActiveTrue();

    long countByGenreIdAndIsActiveTrue(Long genreId);

    long countByAuthorIdAndIsActiveTrue(Long authorId);


    @Modifying
    @Query("UPDATE Book b SET b.isActive = :active WHERE b.id = :bookId")
    void updateActiveStatus(@Param("bookId") Long bookId, @Param("active") Boolean active);


    @Modifying
    @Query("UPDATE Book b SET b.isActive = false WHERE b.id = :bookId")
    void softDeleteById(@Param("bookId") Long bookId);

    // Advanced filter search
    @Query("""
        SELECT b FROM Book b
        WHERE b.isActive = true
        AND (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
        AND (:author IS NULL OR LOWER(CONCAT(b.author.firstName, ' ', b.author.lastName)) LIKE LOWER(CONCAT('%', :author, '%')))
        AND (:genre IS NULL OR LOWER(b.genre.name) LIKE LOWER(CONCAT('%', :genre, '%')))
        AND (:language IS NULL OR LOWER(b.language) = LOWER(:language))
        """)
    Page<Book> findByFilters(
        @Param("title") String title,
        @Param("author") String author,
        @Param("genre") String genre,
        @Param("language") String language,
        Pageable pageable
    );
}
