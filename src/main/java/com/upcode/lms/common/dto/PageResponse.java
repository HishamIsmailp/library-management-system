package com.upcode.lms.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;
    private String sortBy;
    private String sortDirection;
    
    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .sortBy(extractSortBy(page))
                .sortDirection(extractSortDirection(page))
                .build();
    }
    
    public static <T> PageResponse<T> of(Page<T> page, String sortBy, String sortDirection) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
    }
    
    private static <T> String extractSortBy(Page<T> page) {
        if (page.getSort().isSorted()) {
            return page.getSort().iterator().next().getProperty();
        }
        return "id";
    }
    
    private static <T> String extractSortDirection(Page<T> page) {
        if (page.getSort().isSorted()) {
            return page.getSort().iterator().next().getDirection().name();
        }
        return "ASC";
    }
}