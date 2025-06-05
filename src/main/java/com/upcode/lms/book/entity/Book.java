package com.upcode.lms.book.entity;


import com.upcode.lms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book extends BaseEntity {


    private String title;
    private String isbn;
    private String description;
    private LocalDate publicationDate;
    private Integer pages;
    private String language;
    private String edition;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    private Integer totalCopies;
    private Integer availableCopies;

    @ManyToOne
    private Author author;

    @ManyToOne
    private Publisher publisher;

    @ManyToOne
    private  Genre genre;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<BookCopy> copies;

}
