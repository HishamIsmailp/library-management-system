package com.upcode.lms.book.entity;

import com.upcode.lms.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class BookCopy extends BaseEntity {

    @ManyToOne
    private  Book book;

    private String copyNumber;
    private String condition;
    private String location;
    private String status;
    private LocalDateTime acquisitionDate;


}
