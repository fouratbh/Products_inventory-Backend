package com.app.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDto {
    private String name;
    private String description;
    private Long categoryId;
    private Long supplierId;
    
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal purchasePrice;
    
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal sellingPrice;
    
    private Integer minStockLevel;
    private Integer maxStockLevel;
    private String unit;
    private Integer warrantyMonths;
}
