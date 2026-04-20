package com.app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

@Builder
public record ProductDto(Long id, String code, String name, String description, CategoryDto category, SupplierDto supplier, BigDecimal purchasePrice, BigDecimal sellingPrice, Integer quantityInStock, Integer minStockLevel, Integer maxStockLevel, String unit, Integer warrantyMonths, LocalDateTime createdAt, LocalDateTime updatedAt, BigDecimal margin, BigDecimal marginPercentage, Boolean isLowStock, Boolean isOutOfStock, String imageUrl) {
  
}
