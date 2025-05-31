package com.upcode.lms.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Category creation request")
public class CategoryCreateDto {
    
    @Schema(description = "Category name", example = "Programming", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;
    
    @Schema(description = "Category description", example = "Books about software development and programming languages")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @Schema(description = "Color code for UI display", example = "#4CAF50")
    @Size(max = 10, message = "Color code cannot exceed 10 characters")
    private String colorCode;
    
    @Schema(description = "Icon name for UI display", example = "code")
    @Size(max = 50, message = "Icon cannot exceed 50 characters")
    private String icon;
    
    @Schema(description = "Parent category ID for hierarchical structure", example = "1")
    private Long parentCategoryId;
    
    @Schema(description = "Whether category should be featured", example = "true")
    private Boolean isFeatured;
}