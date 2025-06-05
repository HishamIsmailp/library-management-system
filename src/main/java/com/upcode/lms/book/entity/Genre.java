package com.upcode.lms.book.entity;

import com.upcode.lms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "genres")
@Getter
@Setter
public class Genre extends BaseEntity {

    private String name;

    @Column(length = 1000)
    private String description;
}