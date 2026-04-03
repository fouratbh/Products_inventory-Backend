package com.app.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import com.app.dto.PageResponse;

@Mapper(componentModel = "spring")
public interface PageMapper {
    
    default <T, D> PageResponse<D> toPageResponse(Page<T> page, List<D> content) {
        return PageResponse.<D>builder()
            .content(content)
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();
    }
}
