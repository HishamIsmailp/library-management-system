package com.upcode.lms.book.entity;

import com.upcode.lms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "publishers")
@Getter
@Setter
public class Publisher extends BaseEntity {

    private String name;
    private String address;
    private String contactEmail;
    private String contactPhone;
    private String website;
}