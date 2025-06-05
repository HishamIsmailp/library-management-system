package com.upcode.lms.book.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String isbn;
    private String description;

    @Temporal(TemporalType.DATE)
    private Date publicationDate;

    private Integer pages;
    private String language;
    private String edition;
    private String coverImageUrl;

    private Integer totalCopies;
    private Integer availableCopies;

    private Long authorId;
    private Long publisherId;
    private Long genreId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Boolean isActive;

    // Getters and Setters
}