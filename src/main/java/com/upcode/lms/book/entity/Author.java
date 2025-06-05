package com.upcode.lms.book.entity;

import com.upcode.lms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "authors")
@Getter
@Setter
public class Author extends BaseEntity {

    private String firstName;
    private String lastName;

    @Column(length = 1000)
    private String biography;

    private LocalDate birthDate;
    private LocalDate deathDate;
    private String nationality;
}