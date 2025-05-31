package com.upcode.lms.category.controller;

import com.upcode.lms.category.dto.CategoryCreateDto;
import com.upcode.lms.category.dto.CategoryDto;
import com.upcode.lms.category.dto.CategoryUpdateDto;
import com.upcode.lms.category.service.CategoryService;
import com.upcode.lms.common.dto.ApiResponse;
import com.upcode.lms.common.dto.PageRequest;
import com.upcode.lms.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Category Management", description = "APIs for managing book categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CategoryDto>>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "displayOrder") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        log.info("GET /categories - page: {}, size: {}, sortBy: {}, sortDirection: {}", 
                page, size, sortBy, sortDirection);

        PageRequest pageRequest = PageRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        PageResponse<CategoryDto> categories = categoryService.getAllCategories(pageRequest);
        return ResponseEntity.ok(ApiResponse.success(categories, "Categories retrieved successfully"));
    }

    @GetMapping("/root")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getRootCategories() {
        log.info("GET /categories/root");
        
        List<CategoryDto> rootCategories = categoryService.getRootCategories();
        return ResponseEntity.ok(ApiResponse.success(rootCategories, "Root categories retrieved successfully"));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getFeaturedCategories() {
        log.info("GET /categories/featured");
        
        List<CategoryDto> featuredCategories = categoryService.getFeaturedCategories();
        return ResponseEntity.ok(ApiResponse.success(featuredCategories, "Featured categories retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDto>> getCategoryById(@PathVariable Long id) {
        log.info("GET /categories/{}", id);
        
        CategoryDto category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(category, "Category retrieved successfully"));
    }

    @GetMapping("/{id}/subcategories")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getSubCategories(@PathVariable Long id) {
        log.info("GET /categories/{}/subcategories", id);
        
        List<CategoryDto> subCategories = categoryService.getSubCategories(id);
        return ResponseEntity.ok(ApiResponse.success(subCategories, "Sub-categories retrieved successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<CategoryDto>>> searchCategories(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        log.info("GET /categories/search - query: {}, page: {}, size: {}", query, page, size);

        PageRequest pageRequest = PageRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        PageResponse<CategoryDto> searchResults = categoryService.searchCategories(query, pageRequest);
        return ResponseEntity.ok(ApiResponse.success(searchResults, "Categories search completed successfully"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(@Valid @RequestBody CategoryCreateDto createDto) {
        log.info("POST /categories - creating category: {}", createDto.getName());
        
        CategoryDto createdCategory = categoryService.createCategory(createDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdCategory, "Category created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateDto updateDto
    ) {
        log.info("PUT /categories/{} - updating category", id);
        
        CategoryDto updatedCategory = categoryService.updateCategory(id, updateDto);
        return ResponseEntity.ok(ApiResponse.success(updatedCategory, "Category updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        log.info("DELETE /categories/{}", id);
        
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully"));
    }

    @PatchMapping("/{id}/display-order")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<Void>> updateDisplayOrder(
            @PathVariable Long id,
            @RequestParam Integer displayOrder
    ) {
        log.info("PATCH /categories/{}/display-order - new order: {}", id, displayOrder);
        
        categoryService.updateDisplayOrder(id, displayOrder);
        return ResponseEntity.ok(ApiResponse.success("Display order updated successfully"));
    }

    @PatchMapping("/{id}/featured")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<CategoryDto>> toggleFeaturedStatus(@PathVariable Long id) {
        log.info("PATCH /categories/{}/featured", id);
        
        CategoryDto updatedCategory = categoryService.toggleFeaturedStatus(id);
        return ResponseEntity.ok(ApiResponse.success(updatedCategory, "Featured status updated successfully"));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<CategoryService.CategoryStatisticsDto>> getCategoryStatistics() {
        log.info("GET /categories/statistics");
        
        CategoryService.CategoryStatisticsDto statistics = categoryService.getCategoryStatistics();
        return ResponseEntity.ok(ApiResponse.success(statistics, "Statistics retrieved successfully"));
    }
}