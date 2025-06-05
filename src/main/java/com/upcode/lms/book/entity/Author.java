package com.upcode.lms.book.entity;

import com.upcode.lms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Author extends BaseEntity {



    private String firstName;
    private String lastName;
    private String biography;
    private LocalDate birthDate;
    private LocalDate deathDate;
    private String nationality;

    @Transient
    public String getName() {
        return firstName + " " + lastName;
    }



}
