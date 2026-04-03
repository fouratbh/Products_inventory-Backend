package com.app.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dto.CategoryCreateDto;
import com.app.dto.CategoryDto;
import com.app.dto.PageResponse;
import com.app.entities.Category;
import com.app.exceptions.ResourceNotFoundException;
import com.app.mapper.CategoryMapper;
import com.app.mapper.PageMapper;
import com.app.repos.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final PageMapper pageMapper;
    
    @Transactional(readOnly = true)
    public PageResponse<CategoryDto> getAllCategories(Pageable pageable) {
        Page<Category> page = categoryRepository.findAll(pageable);
        List<CategoryDto> content = categoryMapper.toDTOList(page.getContent());
        return pageMapper.toPageResponse(page, content);
    }
    
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategoriesList() {
        return categoryRepository.findAll().stream()
            .map(categoryMapper::toDTO)
            .toList();
    }
    
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return categoryMapper.toDTO(category);
    }
    
    public CategoryDto createCategory(CategoryCreateDto dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Category name already exists: " + dto.getName());
        }
        
        Category category = categoryMapper.toEntity(dto);
        Category savedCategory = categoryRepository.save(category);
        
        log.info("Category created: {}", savedCategory.getName());
        return categoryMapper.toDTO(savedCategory);
    }
    
    public CategoryDto updateCategory(Long id, CategoryCreateDto dto) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        
        if (!category.getName().equals(dto.getName()) && 
            categoryRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Category name already exists: " + dto.getName());
        }
        
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        
        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated: {}", updatedCategory.getName());
        
        return categoryMapper.toDTO(updatedCategory);
    }
    
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findByIdWithProducts(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        
        if (!category.getProducts().isEmpty()) {
            throw new IllegalStateException(
                "Cannot delete category with existing products. " +
                "Reassign or delete products first."
            );
        }
        
        categoryRepository.delete(category);
        log.info("Category deleted with id: {}", id);
    }
}