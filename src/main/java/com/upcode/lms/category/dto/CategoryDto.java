package com.upcode.lms.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Category information")
public class CategoryDto {
    
    @Schema(description = "Category ID", example = "1")
    private Long id;
    
    @Schema(description = "Category name", example = "Science Fiction")
    private String name;
    
    @Schema(description = "Category description", example = "Books about futuristic and scientific themes")
    private String description;
    
    @Schema(description = "Color code for UI", example = "#2196F3")
    private String colorCode;
    
    @Schema(description = "Icon name", example = "rocket")
    private String icon;
    
    @Schema(description = "Display order", example = "1")
    private Integer displayOrder;
    
    @Schema(description = "Whether category is featured", example = "true")
    private Boolean isFeatured;
    
    @Schema(description = "Whether category is active", example = "true")
    private Boolean isActive;
    
    // Parent category info
    @Schema(description = "Parent category ID", example = "2")
    private Long parentCategoryId;
    
    @Schema(description = "Parent category name", example = "Fiction")
    private String parentCategoryName;
    
    // Computed fields
    @Schema(description = "Whether category has sub-categories", example = "false")
    private Boolean hasSubCategories;
    
    @Schema(description = "Depth level in hierarchy", example = "1")
    private Integer depthLevel;
    
    @Schema(description = "Full path in hierarchy", example = "Fiction > Science Fiction")
    private String fullPath;
    
    // Timestamps
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}