package com.app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String code;
    private String name;
    private String description;
    private CategoryDto category;
    private SupplierDto supplier;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private Integer quantityInStock;
    private Integer minStockLevel;
    private Integer maxStockLevel;
    private String unit;
    private Integer warrantyMonths;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal margin;
    private BigDecimal marginPercentage;
    private Boolean isLowStock;
    private Boolean isOutOfStock;
}
