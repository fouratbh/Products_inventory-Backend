package com.app.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dto.PageResponse;
import com.app.dto.ProductCreateDto;
import com.app.dto.ProductDto;
import com.app.dto.ProductStockAdjustmentDto;
import com.app.dto.ProductUpdateDto;
import com.app.entities.Category;
import com.app.entities.Product;
import com.app.entities.Supplier;
import com.app.exceptions.ResourceNotFoundException;
import com.app.mapper.PageMapper;
import com.app.mapper.ProductMapper;
import com.app.repos.CategoryRepository;
import com.app.repos.ProductRepository;
import com.app.repos.SupplierRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final StockMovementService stockMovementService;
    private final ProductMapper productMapper;
    private final PageMapper pageMapper;
    
    @Transactional(readOnly = true)
    public PageResponse<ProductDto> getAllProducts(Pageable pageable) {
        Page<Product> page = productRepository.findAll(pageable);
        List<ProductDto> content = productMapper.toDTOList(page.getContent());
        return pageMapper.toPageResponse(page, content);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<ProductDto> searchProducts(String search, Pageable pageable) {
        Page<Product> page = productRepository.searchProducts(search, pageable);
		List<ProductDto> content = productMapper.toDTOList(page.getContent());
        return pageMapper.toPageResponse(page, content);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<ProductDto> getProductsByCategory(Long categoryId, Pageable pageable) {
        Page<Product> page = productRepository.findByCategoryId(categoryId, pageable);
        List<ProductDto> content = productMapper.toDTOList(page.getContent());
        return pageMapper.toPageResponse(page, content);
    }
    
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toDTO(product);
    }
    
    @Transactional(readOnly = true)
    public ProductDto getProductByCode(String code) {
        Product product = productRepository.findByCode(code)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with code: " + code));
        return productMapper.toDTO(product);
    }
    
    @Transactional(readOnly = true)
    public List<ProductDto> getLowStockProducts() {
        return productRepository.findLowStockProducts().stream()
            .map(productMapper::toDTO)
            .toList();
    }
    
    @Transactional(readOnly = true)
    public List<ProductDto> getOutOfStockProducts() {
        return productRepository.findOutOfStockProducts().stream()
            .map(productMapper::toDTO)
            .toList();
    }
    
    public ProductDto createProduct(ProductCreateDto dto) {
        if (productRepository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("Product code already exists: " + dto.getCode());
        }
        
        Category category = categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));
        
        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
            .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + dto.getSupplierId()));
        
        Product product = Product.builder()
            .code(dto.getCode())
            .name(dto.getName())
            .description(dto.getDescription())
            .category(category)
            .supplier(supplier)
            .purchasePrice(dto.getPurchasePrice())
            .sellingPrice(dto.getSellingPrice())
            .quantityInStock(dto.getQuantityInStock() != null ? dto.getQuantityInStock() : 0)
            .minStockLevel(dto.getMinStockLevel() != null ? dto.getMinStockLevel() : 10)
            .maxStockLevel(dto.getMaxStockLevel() != null ? dto.getMaxStockLevel() : 1000)
            .unit(dto.getUnit() != null ? dto.getUnit() : "pièce")
            .warrantyMonths(dto.getWarrantyMonths() != null ? dto.getWarrantyMonths() : 12)
            .build();
        
        Product savedProduct = productRepository.save(product);
        
        // Create initial stock movement if quantity > 0
        if (savedProduct.getQuantityInStock() > 0) {
            stockMovementService.createInitialStockMovement(
                savedProduct, 
                savedProduct.getQuantityInStock()
            );
        }
        
        log.info("Product created: {} ({})", savedProduct.getName(), savedProduct.getCode());
        return productMapper.toDTO(savedProduct);
    }
    
    public ProductDto updateProduct(Long id, ProductUpdateDto dto) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            product.setCategory(category);
        }
        
        if (dto.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
            product.setSupplier(supplier);
        }
        
        productMapper.updateEntityFromDTO(dto, product);
        
        Product updatedProduct = productRepository.save(product);
        log.info("Product updated: {}", updatedProduct.getCode());
        
        return productMapper.toDTO(updatedProduct);
    }
    
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        if (product.getQuantityInStock() > 0) {
            throw new IllegalStateException(
                "Cannot delete product with stock. Adjust stock to zero first."
            );
        }
        
        productRepository.delete(product);
        log.info("Product deleted: {}", product.getCode());
    }
    
    public ProductDto adjustStock(Long id, ProductStockAdjustmentDto dto) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        int oldQuantity = product.getQuantityInStock();
        int newQuantity = oldQuantity + dto.getQuantity();
        
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Stock adjustment would result in negative stock");
        }
        
        product.setQuantityInStock(newQuantity);
        Product updatedProduct = productRepository.save(product);
        
        stockMovementService.createAdjustmentMovement(
            product, 
            dto.getQuantity(), 
            dto.getNotes()
        );
        
        log.info("Stock adjusted for product {}: {} -> {}", 
            product.getCode(), oldQuantity, newQuantity);
        
        return productMapper.toDTO(updatedProduct);
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getTotalStockValue() {
        BigDecimal total = productRepository.calculateTotalStockValue();
        return total != null ? total : BigDecimal.ZERO;
    }
    
    void updateProductStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        int newQuantity = product.getQuantityInStock() + quantity;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
        }
        
        product.setQuantityInStock(newQuantity);
        productRepository.save(product);
    }
}