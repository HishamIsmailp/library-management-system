package com.upcode.lms.category.service;

import com.upcode.lms.category.dto.CategoryCreateDto;
import com.upcode.lms.category.dto.CategoryDto;
import com.upcode.lms.category.dto.CategoryUpdateDto;
import com.upcode.lms.category.entity.Category;
import com.upcode.lms.category.repository.CategoryRepository;
import com.upcode.lms.common.dto.PageRequest;
import com.upcode.lms.common.dto.PageResponse;
import com.upcode.lms.common.exception.LibraryBusinessException;
import com.upcode.lms.common.exception.ResourceNotFoundException;
import com.upcode.lms.common.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    /**
     * Get all active categories with pagination
     */
    public PageResponse<CategoryDto> getAllCategories(PageRequest pageRequest) {
        log.debug("Fetching all categories with pagination: {}", pageRequest);
        
        Page<Category> categoryPage = categoryRepository.findByIsActiveTrue(
                pageRequest.toPageRequest("displayOrder"));
        
        List<CategoryDto> categoryDtos = categoryPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return PageResponse.of(categoryPage.map(this::convertToDto));
    }

    /**
     * Get all root categories (categories without parent)
     */
    public List<CategoryDto> getRootCategories() {
        log.debug("Fetching all root categories");
        
        List<Category> rootCategories = categoryRepository
                .findByParentCategoryIsNullAndIsActiveTrueOrderByDisplayOrderAscNameAsc();
        
        return rootCategories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get sub-categories by parent ID
     */
    public List<CategoryDto> getSubCategories(Long parentId) {
        log.debug("Fetching sub-categories for parent ID: {}", parentId);
        
        // Verify parent category exists
        findCategoryById(parentId);
        
        List<Category> subCategories = categoryRepository
                .findByParentCategoryIdAndIsActiveTrueOrderByDisplayOrderAscNameAsc(parentId);
        
        return subCategories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get featured categories
     */
    public List<CategoryDto> getFeaturedCategories() {
        log.debug("Fetching featured categories");
        
        List<Category> featuredCategories = categoryRepository
                .findByIsFeaturedTrueAndIsActiveTrueOrderByDisplayOrderAscNameAsc();
        
        return featuredCategories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get category by ID
     */
    public CategoryDto getCategoryById(Long id) {
        log.debug("Fetching category by ID: {}", id);
        
        Category category = findCategoryById(id);
        return convertToDto(category);
    }

    /**
     * Search categories by name or description
     */
    public PageResponse<CategoryDto> searchCategories(String search, PageRequest pageRequest) {
        log.debug("Searching categories with query: {} and pagination: {}", search, pageRequest);
        
        Page<Category> categoryPage = categoryRepository.searchCategories(
                search, pageRequest.toPageRequest("displayOrder"));
        
        return PageResponse.of(categoryPage.map(this::convertToDto));
    }

    /**
     * Create new category
     */
    @Transactional
    public CategoryDto createCategory(CategoryCreateDto createDto) {
        log.debug("Creating new category: {}", createDto);
        
        // Ensure user has permission
        SecurityUtils.ensureStaff();
        
        // Validate category name uniqueness
        validateCategoryNameUniqueness(createDto.getName(), null);
        
        // Validate parent category if provided
        Category parentCategory = null;
        if (createDto.getParentCategoryId() != null) {
            parentCategory = findCategoryById(createDto.getParentCategoryId());
            validateParentCategory(parentCategory);
        }
        
        // Create category
        Category category = Category.builder()
                .name(createDto.getName().trim())
                .description(createDto.getDescription())
                .colorCode(createDto.getColorCode())
                .icon(createDto.getIcon())
                .parentCategory(parentCategory)
                .displayOrder(calculateNextDisplayOrder(createDto.getParentCategoryId()))
                .isFeatured(createDto.getIsFeatured() != null ? createDto.getIsFeatured() : false)
                .build();
        
        Category savedCategory = categoryRepository.save(category);
        log.info("Created category with ID: {} by user: {}", 
                savedCategory.getId(), SecurityUtils.getCurrentUsername().orElse("system"));
        
        return convertToDto(savedCategory);
    }

    /**
     * Update existing category
     */
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryUpdateDto updateDto) {
        log.debug("Updating category ID: {} with data: {}", id, updateDto);
        
        // Ensure user has permission
        SecurityUtils.ensureStaff();
        
        Category category = findCategoryById(id);
        
        // Validate category name uniqueness if name is being changed
        if (!category.getName().equals(updateDto.getName())) {
            validateCategoryNameUniqueness(updateDto.getName(), id);
        }
        
        // Validate parent category if being changed
        if (updateDto.getParentCategoryId() != null) {
            if (!updateDto.getParentCategoryId().equals(
                    category.getParentCategory() != null ? category.getParentCategory().getId() : null)) {
                
                Category newParentCategory = findCategoryById(updateDto.getParentCategoryId());
                validateParentCategory(newParentCategory);
                validateCircularReference(id, updateDto.getParentCategoryId());
                category.setParentCategory(newParentCategory);
            }
        } else if (category.getParentCategory() != null) {
            // Removing parent category
            category.setParentCategory(null);
        }
        
        // Update fields
        category.setName(updateDto.getName().trim());
        category.setDescription(updateDto.getDescription());
        category.setColorCode(updateDto.getColorCode());
        category.setIcon(updateDto.getIcon());
        
        if (updateDto.getIsFeatured() != null) {
            category.setIsFeatured(updateDto.getIsFeatured());
        }
        
        Category updatedCategory = categoryRepository.save(category);
        log.info("Updated category with ID: {} by user: {}", 
                updatedCategory.getId(), SecurityUtils.getCurrentUsername().orElse("system"));
        
        return convertToDto(updatedCategory);
    }

    /**
     * Delete category (soft delete)
     */
    @Transactional
    public void deleteCategory(Long id) {
        log.debug("Deleting category with ID: {}", id);
        
        // Ensure user has permission
        SecurityUtils.ensureStaff();
        
        Category category = findCategoryById(id);
        
        // Check if category has active sub-categories
        if (categoryRepository.hasActiveSubCategories(id)) {
            throw LibraryBusinessException.create(
                    "Cannot delete category with active sub-categories", 
                    "CATEGORY_HAS_SUBCATEGORIES", 
                    "CATEGORY");
        }
        
        // Soft delete
        category.setIsActive(false);
        categoryRepository.save(category);
        
        log.info("Deleted category with ID: {} by user: {}", 
                id, SecurityUtils.getCurrentUsername().orElse("system"));
    }

    /**
     * Update display order of category
     */
    @Transactional
    public void updateDisplayOrder(Long id, Integer newDisplayOrder) {
        log.debug("Updating display order for category ID: {} to: {}", id, newDisplayOrder);
        
        // Ensure user has permission
        SecurityUtils.ensureStaff();
        
        Category category = findCategoryById(id);
        
        if (newDisplayOrder < 0) {
            throw new IllegalArgumentException("Display order cannot be negative");
        }
        
        // Update display order
        categoryRepository.updateDisplayOrder(id, newDisplayOrder);
        
        log.info("Updated display order for category ID: {} to: {} by user: {}", 
                id, newDisplayOrder, SecurityUtils.getCurrentUsername().orElse("system"));
    }

    /**
     * Toggle featured status of category
     */
    @Transactional
    public CategoryDto toggleFeaturedStatus(Long id) {
        log.debug("Toggling featured status for category ID: {}", id);
        
        // Ensure user has permission
        SecurityUtils.ensureStaff();
        
        Category category = findCategoryById(id);
        boolean newFeaturedStatus = !category.getIsFeatured();
        
        categoryRepository.updateFeaturedStatus(id, newFeaturedStatus);
        category.setIsFeatured(newFeaturedStatus);
        
        log.info("Toggled featured status for category ID: {} to: {} by user: {}", 
                id, newFeaturedStatus, SecurityUtils.getCurrentUsername().orElse("system"));
        
        return convertToDto(category);
    }

    /**
     * Get category statistics
     */
    public CategoryStatisticsDto getCategoryStatistics() {
        log.debug("Fetching category statistics");
        
        long totalCategories = categoryRepository.countByIsActiveTrue();
        long rootCategories = categoryRepository.countRootCategories();
        long subCategories = categoryRepository.countSubCategories();
        long featuredCategories = categoryRepository.countByIsFeaturedTrueAndIsActiveTrue();
        
        return CategoryStatisticsDto.builder()
                .totalCategories(totalCategories)
                .rootCategories(rootCategories)
                .subCategories(subCategories)
                .featuredCategories(featuredCategories)
                .build();
    }

    // Helper methods

    private Category findCategoryById(Long id) {
        return categoryRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    private void validateCategoryNameUniqueness(String name, Long excludeId) {
        if (excludeId == null) {
            if (categoryRepository.existsByNameAndIsActiveTrue(name)) {
                throw LibraryBusinessException.create(
                        "Category with name '" + name + "' already exists",
                        "CATEGORY_NAME_EXISTS",
                        "CATEGORY");
            }
        } else {
            if (categoryRepository.existsByNameAndIdNotAndIsActiveTrue(name, excludeId)) {
                throw LibraryBusinessException.create(
                        "Category with name '" + name + "' already exists",
                        "CATEGORY_NAME_EXISTS",
                        "CATEGORY");
            }
        }
    }

    private void validateParentCategory(Category parentCategory) {
        if (!parentCategory.getIsActive()) {
            throw LibraryBusinessException.create(
                    "Cannot set inactive category as parent",
                    "INVALID_PARENT_CATEGORY",
                    "CATEGORY");
        }
    }

    private void validateCircularReference(Long categoryId, Long parentCategoryId) {
        Category current = categoryRepository.findById(parentCategoryId).orElse(null);
        
        while (current != null) {
            if (current.getId().equals(categoryId)) {
                throw LibraryBusinessException.create(
                        "Circular reference detected: category cannot be its own ancestor",
                        "CIRCULAR_REFERENCE",
                        "CATEGORY");
            }
            current = current.getParentCategory();
        }
    }

    private Integer calculateNextDisplayOrder(Long parentCategoryId) {
        Optional<Integer> maxDisplayOrder;
        
        if (parentCategoryId != null) {
            maxDisplayOrder = categoryRepository.findMaxDisplayOrderByParentId(parentCategoryId);
        } else {
            maxDisplayOrder = categoryRepository.findMaxDisplayOrderForRootCategories();
        }
        
        return maxDisplayOrder.orElse(0) + 1;
    }

    private CategoryDto convertToDto(Category category) {
        CategoryDto dto = modelMapper.map(category, CategoryDto.class);
        
        // Set additional computed fields
        dto.setHasSubCategories(category.hasSubCategories());
        dto.setDepthLevel(category.getDepthLevel());
        dto.setFullPath(category.getFullPath());
        
        if (category.getParentCategory() != null) {
            dto.setParentCategoryName(category.getParentCategory().getName());
        }
        
        return dto;
    }

    // Inner class for statistics
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CategoryStatisticsDto {
        private long totalCategories;
        private long rootCategories;
        private long subCategories;
        private long featuredCategories;
    }
}