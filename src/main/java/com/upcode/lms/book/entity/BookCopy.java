package com.upcode.lms.book.entity;

import com.upcode.lms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "book_copies")
@Getter
@Setter
public class BookCopy extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private String copyNumber;
    private String condition;
    private String location;
    private String status;
    private LocalDate acquisitionDate;
}