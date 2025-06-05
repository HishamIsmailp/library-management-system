package com.upcode.lms.book.entity;

import com.upcode.lms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "books")
@Getter
@Setter
public class Book extends BaseEntity {

    private String title;
    private String isbn;

    @Column(length = 2000)
    private String description;

    private LocalDate publicationDate;
    private int pages;
    private String language;
    private String edition;
    private String coverImageUrl;
    private int totalCopies;
    private int availableCopies;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;
}
