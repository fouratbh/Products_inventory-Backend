package com.app.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.dto.ApiResponse;
import com.app.dto.PageResponse;
import com.app.dto.ProductCreateDto;
import com.app.dto.ProductDto;
import com.app.dto.ProductStockAdjustmentDto;
import com.app.dto.ProductUpdateDto;
import com.app.dto.StockMovementDTO;
import com.app.services.FileStorageService;
import com.app.services.ProductService;
import com.app.services.StockMovementService;
import org.springframework.http.MediaType;

import jakarta.validation.Valid;

import org.springframework.data.domain.Sort;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200")
public class ProductController {
    
    private final ProductService productService;
    private final StockMovementService stockMovementService;
    private final FileStorageService fileStorageService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductDto>>> getAllProducts(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) 
            Pageable pageable) {
        PageResponse<ProductDto> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<ProductDto>>> searchProducts(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<ProductDto> products = productService.searchProducts(query, pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<PageResponse<ProductDto>>> getProductsByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<ProductDto> products = productService.getProductsByCategory(categoryId, pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getLowStockProducts() {
        List<ProductDto> products = productService.getLowStockProducts();
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/out-of-stock")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getOutOfStockProducts() {
        List<ProductDto> products = productService.getOutOfStockProducts();
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/total-value")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalStockValue() {
        BigDecimal totalValue = productService.getTotalStockValue();
        return ResponseEntity.ok(ApiResponse.success(totalValue));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable Long id) {
        ProductDto product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product));
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductByCode(@PathVariable String code) {
        ProductDto product = productService.getProductByCode(code);
        return ResponseEntity.ok(ApiResponse.success(product));
    }
    
    @GetMapping("/{id}/movements")
    public ResponseEntity<ApiResponse<List<StockMovementDTO>>> getProductMovements(
            @PathVariable Long id) {
        List<StockMovementDTO> movements = stockMovementService.getProductMovements(id);
        return ResponseEntity.ok(ApiResponse.success(movements));
    }
    
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(
            @RequestPart("image") MultipartFile image,
            @Valid @RequestPart("product") ProductCreateDto dto) { 
        
        String imageName = fileStorageService.save(image);
        
        ProductDto product = productService.createProduct(dto, imageName);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Produit créé avec succès", product));
    }
    
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") @Valid ProductUpdateDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        if (image != null && !image.isEmpty()) {
            String imageName = fileStorageService.save(image);
            dto.setImageUrl(imageName);
        }

        ProductDto product = productService.updateProduct(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Produit mis à jour", product));
    }

    @PutMapping("/{id}/adjust-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE')")
    public ResponseEntity<ApiResponse<ProductDto>> adjustStock(
            @PathVariable Long id,
            @Valid @RequestBody ProductStockAdjustmentDto dto) {
        ProductDto product = productService.adjustStock(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Stock ajusté", product));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Produit supprimé", null));
    }
}
