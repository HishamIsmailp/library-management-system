package com.upcode.lms.category.entity;

import com.upcode.lms.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories", indexes = {
    @Index(name = "idx_category_name", columnList = "name"),
    @Index(name = "idx_category_parent", columnList = "parent_category_id"),
    @Index(name = "idx_category_active", columnList = "is_active")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;

    @Size(max = 10, message = "Color code cannot exceed 10 characters")
    @Column(name = "color_code", length = 10)
    private String colorCode;

    @Size(max = 50, message = "Icon cannot exceed 50 characters")
    @Column(name = "icon", length = 50)
    private String icon;

    @Builder.Default
    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Builder.Default
    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    // Self-referencing relationship for hierarchical categories
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id", foreignKey = @ForeignKey(name = "fk_category_parent"))
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Category> subCategories = new ArrayList<>();

    // Helper methods
    public boolean hasParent() {
        return parentCategory != null;
    }

    public boolean hasSubCategories() {
        return subCategories != null && !subCategories.isEmpty();
    }

    public boolean isRootCategory() {
        return parentCategory == null;
    }

    public boolean isLeafCategory() {
        return subCategories == null || subCategories.isEmpty();
    }

    public String getFullPath() {
        if (parentCategory == null) {
            return name;
        }
        return parentCategory.getFullPath() + " > " + name;
    }

    public int getDepthLevel() {
        if (parentCategory == null) {
            return 0;
        }
        return parentCategory.getDepthLevel() + 1;
    }

    public void addSubCategory(Category subCategory) {
        if (subCategories == null) {
            subCategories = new ArrayList<>();
        }
        subCategories.add(subCategory);
        subCategory.setParentCategory(this);
    }

    public void removeSubCategory(Category subCategory) {
        if (subCategories != null) {
            subCategories.remove(subCategory);
            subCategory.setParentCategory(null);
        }
    }

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (displayOrder == null) {
            displayOrder = 0;
        }
        if (isFeatured == null) {
            isFeatured = false;
        }
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", displayOrder=" + displayOrder +
                ", isFeatured=" + isFeatured +
                ", hasParent=" + hasParent() +
                ", subCategoriesCount=" + (subCategories != null ? subCategories.size() : 0) +
                '}';
    }
}