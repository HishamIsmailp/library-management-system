package com.upcode.lms.category.repository;

import com.upcode.lms.category.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // Basic finders
    Optional<Category> findByNameAndIsActiveTrue(String name);
    
    boolean existsByNameAndIsActiveTrue(String name);
    
    boolean existsByNameAndIdNotAndIsActiveTrue(String name, Long id);
    
    // Active categories
    List<Category> findByIsActiveTrueOrderByDisplayOrderAscNameAsc();
    
    Page<Category> findByIsActiveTrue(Pageable pageable);
    
    // Root categories (no parent)
    List<Category> findByParentCategoryIsNullAndIsActiveTrueOrderByDisplayOrderAscNameAsc();
    
    Page<Category> findByParentCategoryIsNullAndIsActiveTrue(Pageable pageable);
    
    // Sub categories by parent
    List<Category> findByParentCategoryIdAndIsActiveTrueOrderByDisplayOrderAscNameAsc(Long parentId);
    
    Page<Category> findByParentCategoryIdAndIsActiveTrue(Long parentId, Pageable pageable);
    
    // Featured categories
    List<Category> findByIsFeaturedTrueAndIsActiveTrueOrderByDisplayOrderAscNameAsc();
    
    // Search functionality
    @Query("SELECT c FROM Category c WHERE " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "c.isActive = true")
    Page<Category> searchCategories(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT c FROM Category c WHERE " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "c.parentCategory IS NULL AND c.isActive = true")
    Page<Category> searchRootCategories(@Param("search") String search, Pageable pageable);
    
    // Hierarchical queries
    @Query("SELECT c FROM Category c WHERE c.parentCategory.id = :parentId AND c.isActive = true")
    List<Category> findSubCategories(@Param("parentId") Long parentId);
    
    @Query("SELECT c FROM Category c WHERE " +
           "c.parentCategory IS NULL AND " +
           "EXISTS (SELECT sc FROM Category sc WHERE sc.parentCategory = c AND sc.isActive = true) AND " +
           "c.isActive = true " +
           "ORDER BY c.displayOrder ASC, c.name ASC")
    List<Category> findRootCategoriesWithSubCategories();
    
    @Query("SELECT c FROM Category c WHERE " +
           "c.parentCategory IS NULL AND " +
           "NOT EXISTS (SELECT sc FROM Category sc WHERE sc.parentCategory = c AND sc.isActive = true) AND " +
           "c.isActive = true " +
           "ORDER BY c.displayOrder ASC, c.name ASC")
    List<Category> findLeafRootCategories();
    
    // Count queries
    long countByIsActiveTrue();
    
    long countByParentCategoryIsNullAndIsActiveTrue();
    
    long countByParentCategoryIdAndIsActiveTrue(Long parentId);
    
    long countByIsFeaturedTrueAndIsActiveTrue();
    
    // Display order queries
    @Query("SELECT MAX(c.displayOrder) FROM Category c WHERE c.parentCategory.id = :parentId AND c.isActive = true")
    Optional<Integer> findMaxDisplayOrderByParentId(@Param("parentId") Long parentId);
    
    @Query("SELECT MAX(c.displayOrder) FROM Category c WHERE c.parentCategory IS NULL AND c.isActive = true")
    Optional<Integer> findMaxDisplayOrderForRootCategories();
    
    List<Category> findByParentCategoryIdAndDisplayOrderGreaterThanAndIsActiveTrue(
            Long parentId, Integer displayOrder);
    
    List<Category> findByParentCategoryIsNullAndDisplayOrderGreaterThanAndIsActiveTrue(
            Integer displayOrder);
    
    // Update queries
    @Modifying
    @Query("UPDATE Category c SET c.displayOrder = :displayOrder WHERE c.id = :categoryId")
    void updateDisplayOrder(@Param("categoryId") Long categoryId, @Param("displayOrder") Integer displayOrder);
    
    @Modifying
    @Query("UPDATE Category c SET c.displayOrder = c.displayOrder + 1 WHERE " +
           "c.parentCategory.id = :parentId AND c.displayOrder >= :displayOrder AND c.isActive = true")
    void incrementDisplayOrderForSiblings(@Param("parentId") Long parentId, @Param("displayOrder") Integer displayOrder);
    
    @Modifying
    @Query("UPDATE Category c SET c.displayOrder = c.displayOrder + 1 WHERE " +
           "c.parentCategory IS NULL AND c.displayOrder >= :displayOrder AND c.isActive = true")
    void incrementDisplayOrderForRootCategories(@Param("displayOrder") Integer displayOrder);
    
    @Modifying
    @Query("UPDATE Category c SET c.isFeatured = :featured WHERE c.id = :categoryId")
    void updateFeaturedStatus(@Param("categoryId") Long categoryId, @Param("featured") Boolean featured);
    
    @Modifying
    @Query("UPDATE Category c SET c.isActive = :active WHERE c.id = :categoryId")
    void updateActiveStatus(@Param("categoryId") Long categoryId, @Param("active") Boolean active);
    
    // Cascade operations
    @Modifying
    @Query("UPDATE Category c SET c.isActive = false WHERE c.parentCategory.id = :parentId")
    void deactivateSubCategories(@Param("parentId") Long parentId);
    
    @Query("SELECT c FROM Category c WHERE c.parentCategory.id IN :parentIds AND c.isActive = true")
    List<Category> findByParentCategoryIds(@Param("parentIds") List<Long> parentIds);
    
    // Statistics
    @Query("SELECT c.parentCategory.id, COUNT(c) FROM Category c WHERE " +
           "c.parentCategory IS NOT NULL AND c.isActive = true " +
           "GROUP BY c.parentCategory.id")
    List<Object[]> getSubCategoryCountsByParent();
    
    @Query("SELECT COUNT(c) FROM Category c WHERE c.parentCategory IS NULL AND c.isActive = true")
    long countRootCategories();
    
    @Query("SELECT COUNT(c) FROM Category c WHERE c.parentCategory IS NOT NULL AND c.isActive = true")
    long countSubCategories();
    
    // Validation queries
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE " +
           "c.parentCategory.id = :categoryId AND c.isActive = true")
    boolean hasActiveSubCategories(@Param("categoryId") Long categoryId);
    
    @Query("SELECT c FROM Category c WHERE c.id = :categoryId AND c.isActive = true")
    Optional<Category> findActiveById(@Param("categoryId") Long categoryId);
}