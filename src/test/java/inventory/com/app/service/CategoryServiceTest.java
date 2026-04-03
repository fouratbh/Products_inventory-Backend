package inventory.com.app.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.app.dto.CategoryCreateDto;
import com.app.dto.CategoryDto;
import com.app.dto.PageResponse;
import com.app.entities.Category;
import com.app.entities.Product;
import com.app.exceptions.ResourceNotFoundException;
import com.app.mapper.CategoryMapper;
import com.app.mapper.PageMapper;
import com.app.repos.CategoryRepository;
import com.app.services.CategoryService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// ============= CATEGORY SERVICE TESTS =============
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private CategoryMapper categoryMapper;
    
    @Mock
    private PageMapper pageMapper;
    
    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;
    private CategoryDto CategoryDto;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
            .id(1L)
            .name("Electronics")
            .description("Electronic devices")
            .build();

        CategoryDto = CategoryDto.builder()
            .id(1L)
            .name("Electronics")
            .description("Electronic devices")
            .productCount(0)
            .build();
    }

    @Test
    @DisplayName("Should get all categories with pagination")
    void shouldGetAllCategoriesWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Category> categoryPage = new PageImpl<>(List.of(testCategory));
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDTOList(anyList())).thenReturn(List.of(CategoryDto));
        when(pageMapper.toPageResponse(any(Page.class), anyList()))
        .thenReturn(PageResponse.<CategoryDto>builder()
            .content(List.of(CategoryDto)) // Attention : 'categoryDto' avec un 'c' minuscule (ta variable)
            .totalElements(1L)
            .build()
        );

        // When
        PageResponse<CategoryDto> result = categoryService.getAllCategories(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Electronics");
        
        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).toDTOList(anyList());
    }

    @Test
    @DisplayName("Should get all categories as list")
    void shouldGetAllCategoriesAsList() {
        // Given
        when(categoryRepository.findAll()).thenReturn(List.of(testCategory));
        when(categoryMapper.toDTO(testCategory)).thenReturn(CategoryDto);

        // When
        List<CategoryDto> result = categoryService.getAllCategoriesList();

        // Then
        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Electronics");
        
        verify(categoryRepository).findAll();
    }

    @Test
    @DisplayName("Should get category by id")
    void shouldGetCategoryById() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryMapper.toDTO(testCategory)).thenReturn(CategoryDto);

        // When
        CategoryDto result = categoryService.getCategoryById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Electronics");
        
        verify(categoryRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when category not found")
    void shouldThrowExceptionWhenCategoryNotFound() {
        // Given
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> categoryService.getCategoryById(999L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Category not found with id: 999");
    }

    @Test
    @DisplayName("Should create category successfully")
    void shouldCreateCategorySuccessfully() {
        // Given
        CategoryCreateDto createDTO = CategoryCreateDto.builder()
            .name("New Category")
            .description("Description")
            .build();

        when(categoryRepository.existsByName("New Category")).thenReturn(false);
        when(categoryMapper.toEntity(createDTO)).thenReturn(testCategory);
        when(categoryRepository.save(testCategory)).thenReturn(testCategory);
        when(categoryMapper.toDTO(testCategory)).thenReturn(CategoryDto);

        // When
        CategoryDto result = categoryService.createCategory(createDTO);

        // Then
        assertThat(result).isNotNull();
        verify(categoryRepository).existsByName("New Category");
        verify(categoryRepository).save(testCategory);
    }

    @Test
    @DisplayName("Should throw exception when creating category with existing name")
    void shouldThrowExceptionWhenCreatingCategoryWithExistingName() {
        // Given
        CategoryCreateDto createDTO = CategoryCreateDto.builder()
            .name("Existing Category")
            .build();

        when(categoryRepository.existsByName("Existing Category")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> categoryService.createCategory(createDTO))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Category name already exists");
        
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update category successfully")
    void shouldUpdateCategorySuccessfully() {
        // Given
        CategoryCreateDto updateDTO = CategoryCreateDto.builder()
            .name("Updated Category")
            .description("Updated description")
            .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.existsByName("Updated Category")).thenReturn(false);
        when(categoryRepository.save(testCategory)).thenReturn(testCategory);
        when(categoryMapper.toDTO(testCategory)).thenReturn(CategoryDto);

        // When
        CategoryDto result = categoryService.updateCategory(1L, updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(testCategory);
    }

    @Test
    @DisplayName("Should delete category successfully")
    void shouldDeleteCategorySuccessfully() {
        // Given
        when(categoryRepository.findByIdWithProducts(1L)).thenReturn(Optional.of(testCategory));

        // When
        categoryService.deleteCategory(1L);

        // Then
        verify(categoryRepository).findByIdWithProducts(1L);
        verify(categoryRepository).delete(testCategory);
    }

    @Test
    @DisplayName("Should throw exception when deleting category with products")
    void shouldThrowExceptionWhenDeletingCategoryWithProducts() {
        // Given
        Product product = Product.builder().id(1L).build();
        testCategory.getProducts().add(product);
        when(categoryRepository.findByIdWithProducts(1L)).thenReturn(Optional.of(testCategory));

        // When/Then
        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot delete category with existing products");
        
        verify(categoryRepository, never()).delete(any());
    }
}
