package com.upcode.lms.book.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookDto {

    private Long id;
    private String title;
    private String isbn;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publicationDate;

    private Integer pages;
    private String language;
    private String edition;
    private String coverImageUrl;

    private Integer totalCopies;
    private Integer availableCopies;


    private Long authorId;
    private Long publisherId;
    private Long genreId;


    private String authorName;
    private String publisherName;
    private String genreName;
}
