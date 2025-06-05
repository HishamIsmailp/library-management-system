package com.upcode.lms.book.dto;

import lombok.Data;

@Data
public class BookSearchDto {
    private String title;
    private String authorName;
    private String genreName;
    private String language;

    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "title";
    private String sortDirection = "ASC";
}
