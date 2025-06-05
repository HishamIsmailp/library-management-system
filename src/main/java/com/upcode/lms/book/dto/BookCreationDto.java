package com.upcode.lms.book.dto;
import jakarta.validation.constraints.*;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class BookCreationDto {
    @NotBlank
    private String title;

    @NotBlank
    private String isbn;

    private String description;

    @PastOrPresent(message = "Publication date can't be in the future")
    private LocalDate publicationDate;

    @Min(1)
    private Integer pages;

    @NotBlank
    private String language;

    private String edition;

    private String coverImageUrl;

    @Min(1)
    private Integer totalCopies;

    private MultipartFile coverImageFile;

    @NotNull
    private Long authorId;

    @NotNull
    private Long publisherId;

    @NotNull
    private Long genreId;
}
