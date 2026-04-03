package com.app.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.app.dto.CategoryCreateDto;
import com.app.dto.CategoryDto;
import com.app.entities.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    @Mapping(target = "productCount", expression = "java(category.getProducts() != null ? category.getProducts().size() : 0)")
    CategoryDto toDTO(Category category);
    
    List<CategoryDto> toDTOList(List<Category> categories);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(CategoryCreateDto dto);
}
