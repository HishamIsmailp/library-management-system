package com.upcode.lms.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {
    
    @Min(value = 0, message = "Page number cannot be negative")
    @Builder.Default
    private int page = 0;
    
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    @Builder.Default
    private int size = 10;
    
    @Builder.Default
    private String sortBy = "id";
    
    @Builder.Default
    private String sortDirection = "ASC";
    
    public org.springframework.data.domain.PageRequest toPageRequest() {
        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by(
            "DESC".equalsIgnoreCase(sortDirection) 
                ? org.springframework.data.domain.Sort.Direction.DESC 
                : org.springframework.data.domain.Sort.Direction.ASC,
            sortBy
        );
        
        return org.springframework.data.domain.PageRequest.of(page, size, sort);
    }
    
    public org.springframework.data.domain.PageRequest toPageRequest(String defaultSortBy) {
        String actualSortBy = (sortBy == null || sortBy.trim().isEmpty()) ? defaultSortBy : sortBy;
        
        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by(
            "DESC".equalsIgnoreCase(sortDirection) 
                ? org.springframework.data.domain.Sort.Direction.DESC 
                : org.springframework.data.domain.Sort.Direction.ASC,
            actualSortBy
        );
        
        return org.springframework.data.domain.PageRequest.of(page, size, sort);
    }
    
    public boolean isValidSortDirection() {
        return "ASC".equalsIgnoreCase(sortDirection) || "DESC".equalsIgnoreCase(sortDirection);
    }
}