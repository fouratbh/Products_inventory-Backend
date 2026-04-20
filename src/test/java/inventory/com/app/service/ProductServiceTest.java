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

import com.app.dto.PageResponse;
import com.app.dto.ProductCreateDto;
import com.app.dto.ProductDto;
import com.app.dto.ProductStockAdjustmentDto;
import com.app.dto.ProductUpdateDto;
import com.app.entities.Category;
import com.app.entities.Product;
import com.app.entities.Supplier;
import com.app.mapper.PageMapper;
import com.app.mapper.ProductMapper;
import com.app.repos.CategoryRepository;
import com.app.repos.ProductRepository;
import com.app.repos.SupplierRepository;
import com.app.services.ProductService;
import com.app.services.StockMovementService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private SupplierRepository supplierRepository;
    
    @Mock
    private StockMovementService stockMovementService;
    
    @Mock
    private ProductMapper productMapper;
    
    @Mock
    private PageMapper pageMapper;
    
    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductDto ProductDto;
    private Category testCategory;
    private Supplier testSupplier;
    private String imageName = "test-image.jpg";

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
            .id(1L)
            .name("Electronics")
            .build();

        testSupplier = Supplier.builder()
            .id(1L)
            .name("Tech Supplier")
            .build();

        testProduct = Product.builder()
            .id(1L)
            .code("PROD001")
            .name("Laptop")
            .description("High-end laptop")
            .category(testCategory)
            .supplier(testSupplier)
            .purchasePrice(new BigDecimal("1000"))
            .sellingPrice(new BigDecimal("1500"))
            .quantityInStock(10)
            .minStockLevel(5)
            .maxStockLevel(100)
            .unit("pièce")
            .warrantyMonths(24)
            .build();

        ProductDto = ProductDto.builder()
            .id(1L)
            .code("PROD001")
            .name("Laptop")
            .quantityInStock(10)
            .purchasePrice(new BigDecimal("1000"))
            .sellingPrice(new BigDecimal("1500"))
            .isLowStock(false)
            .isOutOfStock(false)
            .build();
    }

    @Test
    @DisplayName("Should get all products with pagination")
    void shouldGetAllProductsWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> productPage = new PageImpl<>(List.of(testProduct));
        when(productRepository.findAll(pageable)).thenReturn(productPage);
        when(productMapper.toDTOList(anyList())).thenReturn(List.of(ProductDto));
        when(pageMapper.toPageResponse(any(Page.class), anyList())).thenReturn(
            PageResponse.<ProductDto>builder()
                .content(List.of(ProductDto))
                .totalElements(1L)
                .build()
        );

        // When
        PageResponse<ProductDto> result = productService.getAllProducts(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(productRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should search products")
    void shouldSearchProducts() {
        // Given
        String query = "laptop";
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> productPage = new PageImpl<>(List.of(testProduct));
        when(productRepository.searchProducts(query, pageable)).thenReturn(productPage);
        when(productMapper.toDTOList(anyList())).thenReturn(List.of(ProductDto));
        when(pageMapper.toPageResponse(any(Page.class), anyList())).thenReturn(
            PageResponse.<ProductDto>builder()
                .content(List.of(ProductDto))
                .totalElements(1L)
                .build()
        );

        // When
        PageResponse<ProductDto> result = productService.searchProducts(query, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(productRepository).searchProducts(query, pageable);
    }

    @Test
    @DisplayName("Should get product by id")
    void shouldGetProductById() {
        // Given
        when(productRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(testProduct));
        when(productMapper.toDTO(testProduct)).thenReturn(ProductDto);

        // When
        ProductDto result = productService.getProductById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        verify(productRepository).findByIdWithDetails(1L);
    }

    @Test
    @DisplayName("Should get product by code")
    void shouldGetProductByCode() {
        // Given
        when(productRepository.findByCode("PROD001")).thenReturn(Optional.of(testProduct));
        when(productMapper.toDTO(testProduct)).thenReturn(ProductDto);

        // When
        ProductDto result = productService.getProductByCode("PROD001");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.code()).isEqualTo("PROD001");
        verify(productRepository).findByCode("PROD001");
    }

    @Test
    @DisplayName("Should get low stock products")
    void shouldGetLowStockProducts() {
        // Given
        testProduct.setQuantityInStock(3); // Below minStockLevel (5)
        when(productRepository.findLowStockProducts()).thenReturn(List.of(testProduct));
        when(productMapper.toDTO(testProduct)).thenReturn(ProductDto);

        // When
        List<ProductDto> result = productService.getLowStockProducts();

        // Then
        assertThat(result).isNotNull().hasSize(1);
        verify(productRepository).findLowStockProducts();
    }

    @Test
    @DisplayName("Should get out of stock products")
    void shouldGetOutOfStockProducts() {
        // Given
        testProduct.setQuantityInStock(0);
        when(productRepository.findOutOfStockProducts()).thenReturn(List.of(testProduct));
        when(productMapper.toDTO(testProduct)).thenReturn(ProductDto);

        // When
        List<ProductDto> result = productService.getOutOfStockProducts();

        // Then
        assertThat(result).isNotNull().hasSize(1);
        verify(productRepository).findOutOfStockProducts();
    }

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() {
        // Given
        ProductCreateDto createDTO = ProductCreateDto.builder()
            .code("PROD002")
            .name("Mouse")
            .categoryId(1L)
            .supplierId(1L)
            .purchasePrice(new BigDecimal("20"))
            .sellingPrice(new BigDecimal("30"))
            .quantityInStock(50)
            .build();

        when(productRepository.existsByCode("PROD002")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.toDTO(any(Product.class))).thenReturn(ProductDto);

        // When
        ProductDto result = productService.createProduct(createDTO,imageName);

        // Then
        assertThat(result).isNotNull();
        verify(productRepository).existsByCode("PROD002");
        verify(categoryRepository).findById(1L);
        verify(supplierRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
        verify(stockMovementService).createInitialStockMovement(any(Product.class), eq(10));
    }

    @Test
    @DisplayName("Should throw exception when creating product with existing code")
    void shouldThrowExceptionWhenCreatingProductWithExistingCode() {
        // Given
        ProductCreateDto createDTO = ProductCreateDto.builder()
            .code("EXISTING")
            .name("Product")
            .categoryId(1L)
            .supplierId(1L)
            .purchasePrice(new BigDecimal("100"))
            .sellingPrice(new BigDecimal("150"))
            .build();

        when(productRepository.existsByCode("EXISTING")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> productService.createProduct(createDTO,imageName))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Product code already exists");
        
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() {
        // Given
        ProductUpdateDto updateDTO = ProductUpdateDto.builder()
            .name("Updated Laptop")
            .sellingPrice(new BigDecimal("1600"))
            .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(testProduct)).thenReturn(testProduct);
        when(productMapper.toDTO(testProduct)).thenReturn(ProductDto);

        // When
        ProductDto result = productService.updateProduct(1L, updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(productRepository).findById(1L);
        verify(productMapper).updateEntityFromDTO(updateDTO, testProduct);
        verify(productRepository).save(testProduct);
    }

    @Test
    @DisplayName("Should delete product successfully")
    void shouldDeleteProductSuccessfully() {
        // Given
        testProduct.setQuantityInStock(0);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository).findById(1L);
        verify(productRepository).delete(testProduct);
    }

    @Test
    @DisplayName("Should throw exception when deleting product with stock")
    void shouldThrowExceptionWhenDeletingProductWithStock() {
        // Given
        testProduct.setQuantityInStock(10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When/Then
        assertThatThrownBy(() -> productService.deleteProduct(1L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot delete product with stock");
        
        verify(productRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should adjust stock successfully")
    void shouldAdjustStockSuccessfully() {
        // Given
        ProductStockAdjustmentDto adjustmentDTO = ProductStockAdjustmentDto.builder()
            .quantity(5)
            .notes("Adding stock")
            .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(testProduct)).thenReturn(testProduct);
        when(productMapper.toDTO(testProduct)).thenReturn(ProductDto);

        // When
        ProductDto result = productService.adjustStock(1L, adjustmentDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(testProduct.getQuantityInStock()).isEqualTo(15); // 10 + 5
        verify(stockMovementService).createAdjustmentMovement(testProduct, 5, "Adding stock");
        verify(productRepository).save(testProduct);
    }

    @Test
    @DisplayName("Should throw exception when stock adjustment results in negative stock")
    void shouldThrowExceptionWhenStockAdjustmentResultsInNegativeStock() {
        // Given
        ProductStockAdjustmentDto adjustmentDTO = ProductStockAdjustmentDto.builder()
            .quantity(-15) // Current stock is 10
            .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When/Then
        assertThatThrownBy(() -> productService.adjustStock(1L, adjustmentDTO))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Stock adjustment would result in negative stock");
        
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get total stock value")
    void shouldGetTotalStockValue() {
        // Given
        BigDecimal totalValue = new BigDecimal("10000.00");
        when(productRepository.calculateTotalStockValue()).thenReturn(totalValue);

        // When
        BigDecimal result = productService.getTotalStockValue();

        // Then
        assertThat(result).isEqualByComparingTo(totalValue);
        verify(productRepository).calculateTotalStockValue();
    }

    @Test
    @DisplayName("Should return zero when total stock value is null")
    void shouldReturnZeroWhenTotalStockValueIsNull() {
        // Given
        when(productRepository.calculateTotalStockValue()).thenReturn(null);

        // When
        BigDecimal result = productService.getTotalStockValue();

        // Then
        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
