package com.upcode.lms.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUpdateDto {
    
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @Size(max = 10, message = "Color code cannot exceed 10 characters")
    private String colorCode;
    
    @Size(max = 50, message = "Icon cannot exceed 50 characters")
    private String icon;
    
    private Long parentCategoryId;
    
    private Boolean isFeatured;
}